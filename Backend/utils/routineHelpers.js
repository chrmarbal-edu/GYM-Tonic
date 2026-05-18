const buildRoutineImagePath = (files) => {
    if (files?.image?.[0]) {
        return `images/routines/${files.image[0].filename}`
    }
    return null
}

/** Ruta relativa servida por express.static("public") → /images/routines/... */
const normalizeRoutineImageForClient = (image) => {
    if (!image || typeof image !== "string") {
        return null
    }

    let normalized = image.trim().replace(/\\/g, "/")
    if (!normalized) {
        return null
    }

    if (/^https?:\/\//i.test(normalized)) {
        return normalized
    }

    normalized = normalized.replace(/^\/+/, "").replace(/^public\//, "")

    if (!normalized.startsWith("images/")) {
        const fileName = normalized.includes("/")
            ? normalized.split("/").pop()
            : normalized
        normalized = `images/routines/${fileName}`
    }

    if (!/\.[a-z0-9]+$/i.test(normalized)) {
        normalized = `${normalized}.png`
    }

    return normalized
}

const parseExerciseIds = (value) => {
    if (value === undefined || value === null || value === "") {
        return null
    }

    if (Array.isArray(value)) {
        return value.map((item) => Number(item))
    }

    if (typeof value === "string") {
        try {
            const parsed = JSON.parse(value)
            if (Array.isArray(parsed)) {
                return parsed.map((item) => Number(item))
            }
        } catch {
            // fallback to comma-separated values
        }

        return value
            .split(",")
            .map((item) => Number(item.trim()))
            .filter((item) => Number.isFinite(item))
    }

    return null
}

module.exports = {
    buildRoutineImagePath,
    normalizeRoutineImageForClient,
    parseExerciseIds
}
