var status = -1;

//this quest is SECRET ORGANIZATION SUSPICION
function start(mode, type, selection) {
    qm.sendNext("Let's talk to Mir.");
    qm.forceStartQuest();
    qm.getPlayer().gainSp(1);
    qm.forceCompleteQuest();
    qm.dispose();
}

function end(mode, type, selection) {
    qm.getPlayer().gainSp(1);
    qm.forceCompleteQuest();
    qm.dispose();
}