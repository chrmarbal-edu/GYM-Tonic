const { google } = require('googleapis')
const { Readable } = require('stream')
const path = require('path')
const AppError = require('../utils/AppError')

const GYM_TONIC_ROOT_ID = "1tbofka4pGt2_3_Idd3Df877NFsLGmt90"

const auth = new google.auth.GoogleAuth({
    keyFile: path.join(__dirname, "../private/credentials.json"),
    scopes: ["https://www.googleapis.com/auth/drive"]
})

async function getOrCreateFolder(drive, folderName, parentId) {
    try {
        const query = `'${parentId}' in parents and name='${folderName}' and mimeType='application/vnd.google-apps.folder' and trashed=false`
        const { data } = await drive.files.list({
            q: query,
            fields: "files(id, name)"
        })

        if (data.files && data.files.length > 0) {
            return data.files[0].id
        }

        const { data: newFolder } = await drive.files.create({
            requestBody: {
                name: folderName,
                mimeType: "application/vnd.google-apps.folder",
                parents: [parentId]
            },
            fields: "id"
        })

        return newFolder.id
    } catch (error) {
        console.error(`Error getting/creating folder ${folderName}:`, error)
        throw error
    }
}

async function resolveFolderPath(drive, type, target, username = null) {
    const typeFolderId = await getOrCreateFolder(drive, type, GYM_TONIC_ROOT_ID)
    const targetFolderId = await getOrCreateFolder(drive, target, typeFolderId)

    if (target === 'accounts' && username) {
        const userFolderId = await getOrCreateFolder(drive, username, targetFolderId)
        return userFolderId
    }

    return targetFolderId
}

exports.uploadMedia = async (fileObject, type, target, username = null) => {
    try {
        const authClient = await auth.getClient()
        const drive = google.drive({ version: "v3", auth: authClient })

        const folderId = await resolveFolderPath(drive, type, target, username)

        const fileMetadata = {
            name: `${Date.now()}-${fileObject.originalname}`,
            parents: [folderId]
        }

        const media = {
            mimeType: fileObject.mimetype,
            body: Readable.from(fileObject.buffer)
        }

        const { data } = await drive.files.create({
            requestBody: fileMetadata,
            media: media,
            fields: "id"
        })

        await drive.permissions.create({
            fileId: data.id,
            requestBody: {
                role: 'reader',
                type: 'anyone'
            }
        })

        return data.id
    } catch (error) {
        console.error("Error in uploadMedia:", error)
        throw error
    }
}

exports.streamMedia = async (req, res, next) => {
    const { fileId } = req.params

    try {
        const authClient = await auth.getClient()
        const drive = google.drive({ version: "v3", auth: authClient })

        const metadata = await drive.files.get({
            fileId: fileId,
            fields: "name, mimeType"
        })

        const fileName = metadata.data.name
        const mimeType = metadata.data.mimeType || "application/octet-stream"

        res.setHeader("Content-Disposition", `inline; filename="${fileName}"`)
        res.setHeader("Content-Type", mimeType)

        const driveResponse = await drive.files.get(
            { fileId: fileId, alt: "media" },
            { responseType: "stream" }
        )

        driveResponse.data
            .on("error", (err) => {
                console.error("Error in streaming:", err)
                res.status(500).json({ error: "Could not stream the file" })
            })
            .pipe(res)

    } catch (error) {
        next(new AppError(error, 500))
    }
}