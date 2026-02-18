let leaderLines = [];

function addLine (startId, endId){
    const start = document.getElementById(startId);
    const end = document.getElementById(endId);

    if (!start || !end) return;

    const line = new LeaderLine(start, end, {
        path: 'fluid',
        color: 'grey',
        endPlug: 'behind',
        startSocket: 'bottom',
        startSocketGravity: 50,
        endSocketGravity: 50,
        endSocket: 'top'
    });

    leaderLines.push(line);
    return line;
}

function repositionLeaderLines() {
    console.log("Repositioning lines.");
    leaderLines.forEach(line => safeReposition(line));
}

function safeReposition(line) {
    if (!line || !line.start || !line.end) return;

    if (!document.body.contains(line.start) ||
        !document.body.contains(line.end)) {
        line.remove();
        return;
    }

    line.position();
}

function resetLeaderLines(notes) {
//     Remove existing lines
    leaderLines.forEach(line => line.remove());
    leaderLines = [];

// redraw all lines
    visualizeLeaderLines(notes)
}
