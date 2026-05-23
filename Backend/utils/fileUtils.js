const fs = require("fs").promises;
const path = require("path");

/**
 * Checks if a given path corresponds to a default resource (e.g., default profile picture).
 * @param {string} resourcePath - The relative path of the resource (e.g., 'images/users/default/user.jpg').
 * @returns {boolean} True if it's a default resource, false otherwise.
 */
function isDefaultResourcePath(resourcePath) {
    if (!resourcePath || typeof resourcePath !== "string") {
        return true; // Treat null/empty paths as "default" (i.e., don't try to delete)
    }
    const normalizedPath = resourcePath.replace(/\\/g, "/").toLowerCase();
    return normalizedPath.includes("default/");
}

/**
 * Deletes a file from the public directory if it's not a default resource.
 * Logs an error if deletion fails but does not throw.
 * @param {string} relativePath - The relative path of the file within the 'public' directory (e.g., 'images/users/custom/user1.jpg').
 */
async function deleteResourceFile(relativePath) {
    if (isDefaultResourcePath(relativePath)) {
        return; // Do not delete default resources
    }

    const fullPath = path.join(__dirname, "..", "public", relativePath).replace(/\\/g, "/");

    try {
        await fs.access(fullPath);
        await fs.unlink(fullPath);
    } catch (e) {
        console.error(`Failed to delete resource file: ${fullPath}. Error: ${e.message}`);
    }
}

module.exports = {
    isDefaultResourcePath,
    deleteResourceFile
};