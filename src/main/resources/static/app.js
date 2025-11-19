// add function to window to access it globally
window.saveThread = async function(id, value, parentId) {
    try {
        const formData = new FormData();
        formData.append('id', id);
        formData.append('parentId', parentId);
        formData.append('note', value);

        const response = await fetch('/save', {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            console.log('Note saved!');
        } else {
            console.error('Failed to save note');
        }
    } catch (err) {
        console.error('Error saving note:', err);
    }
};
