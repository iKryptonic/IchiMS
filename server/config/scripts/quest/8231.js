/* Author: Xterminator (Modified by RMZero213)
	NPC Name: 		Roger
	Map(s): 		Maple Road : Lower level of the Training Camp (2)
	Description: 		Quest - Roger's Apple
	FOOLS GOLD
*/
var status = -1;

function start(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
    } else {
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            qm.sendNext("With all the strange occurrences in Masteria and New Leaf City, Lita Lawless must be busy! I'll bet that she's willing to accept my help...maybe I can earn some mesos in the process! A quick jaunt to the Kerning City subway and I'll be on my way to New Leaf City!");
        } else if (status == 1) {
            qm.sendNextPrev("I spoke with Lita, and it looks like there a trickster roaming about the Phantom Forest. It caused her quite a bit of trouble-surprising for a warrior of her caliber. I've agreed to help rid the Forest of these nefarious creatures, and as proof, I have to bring her 30 of the strange silver clovers-she called them Lucky Charms. I'll be on guard-that forest is haunted...or so I've heard...");
        } else if (status == 2) {
            qm.sendAcceptDecline("So you'll help me?");
        } else if (status == 3) {
            qm.forceStartQuest();
            qm.dispose();
        }
    }
}

function end(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
    } else {
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            if (qm.getPlayer().haveItem(4032031) == 30)
                qm.sendNext("Hey, your HP is not fully recovered yet. Did you take all the Roger's Apple that I gave you? Are you sure?");
            qm.gainItem(4032031, -30);
            qm.gainExp(5400);
            qm.gainMeso(100000);
            qm.completeQuest(8231);
            qm.dispose();
            //} else {
            //qm.sendNext("How easy is it to consume the item? Simple, right? You can set a #bhotkey#k on the right bottom slot. Haha you didn't know that! right? Oh, and if you are a beginner, HP will automatically recover itself as time goes by. Well it takes time but this is one of the strategies for the beginners.");
            //	}/
            //} else if (status == 1) {
            //	qm.sendNextPrev("Alright! Now that you have learned alot, I will give you a present. This is a must for your travel in Maple World, so thank me! Please use this under emergency cases!");
            //} else if (status == 2) {
            //	qm.sendNextPrev("Okay, this is all I can teach you. I know it's sad but it is time to say good bye. Well take care if yourself and Good luck my friend!\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#v2010000# 3 #t2010000#\r\n#v2010009# 3 #t2010009#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 10 exp");
            //} else if (status == 3) {
            //	qm.gainExp(10);
            //qm.gainItem(2010000, 3);
            //qm.gainItem(2010009, 3);
            //qm.forceCompleteQuest();
            ///qm.dispose();

        }
    }
}