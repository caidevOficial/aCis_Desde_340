/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests.Q355_FamilyHonor;

import java.util.HashMap;
import java.util.Map;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.util.Rnd;

public class Q355_FamilyHonor extends Quest
{
	private static final String qn = "Q355_FamilyHonor";
	
	// NPCs
	private static final int GALIBREDO = 30181;
	private static final int PATRIN = 30929;
	
	// Monsters
	private static final int TIMAK_ORC_TROOP_LEADER = 20767;
	private static final int TIMAK_ORC_TROOP_SHAMAN = 20768;
	private static final int TIMAK_ORC_TROOP_WARRIOR = 20769;
	private static final int TIMAK_ORC_TROOP_ARCHER = 20770;
	
	// Items
	private static final int GALIBREDO_BUST = 4252;
	private static final int WORK_OF_BERONA = 4350;
	private static final int STATUE_PROTOTYPE = 4351;
	private static final int STATUE_ORIGINAL = 4352;
	private static final int STATUE_REPLICA = 4353;
	private static final int STATUE_FORGERY = 4354;
	
	// Drop chances
	private static final Map<Integer, int[]> CHANCES = new HashMap<>();
	{
		CHANCES.put(TIMAK_ORC_TROOP_LEADER, new int[]
		{
			44,
			54
		});
		CHANCES.put(TIMAK_ORC_TROOP_SHAMAN, new int[]
		{
			36,
			45
		});
		CHANCES.put(TIMAK_ORC_TROOP_WARRIOR, new int[]
		{
			35,
			43
		});
		CHANCES.put(TIMAK_ORC_TROOP_ARCHER, new int[]
		{
			32,
			42
		});
	}
	
	public Q355_FamilyHonor()
	{
		super(355, qn, "Family Honor");
		
		setItemsIds(GALIBREDO_BUST);
		
		addStartNpc(GALIBREDO);
		addTalkId(GALIBREDO, PATRIN);
		
		addKillId(TIMAK_ORC_TROOP_LEADER, TIMAK_ORC_TROOP_SHAMAN, TIMAK_ORC_TROOP_WARRIOR, TIMAK_ORC_TROOP_ARCHER);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30181-2.htm"))
		{
			st.setState(STATE_STARTED);
			st.set("cond", "1");
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30181-4b.htm"))
		{
			final int count = st.getQuestItemsCount(GALIBREDO_BUST);
			if (count > 0)
			{
				htmltext = "30181-4.htm";
				
				int reward = 2800 + (count * 120);
				if (count >= 100)
				{
					htmltext = "30181-4a.htm";
					reward += 5000;
				}
				
				st.takeItems(GALIBREDO_BUST, count);
				st.rewardItems(57, reward);
			}
		}
		else if (event.equalsIgnoreCase("30929-7.htm"))
		{
			if (st.hasQuestItems(WORK_OF_BERONA))
			{
				st.takeItems(WORK_OF_BERONA, 1);
				
				final int appraising = Rnd.get(100);
				if (appraising < 20)
					htmltext = "30929-2.htm";
				else if (appraising < 40)
				{
					htmltext = "30929-3.htm";
					st.giveItems(STATUE_REPLICA, 1);
				}
				else if (appraising < 60)
				{
					htmltext = "30929-4.htm";
					st.giveItems(STATUE_ORIGINAL, 1);
				}
				else if (appraising < 80)
				{
					htmltext = "30929-5.htm";
					st.giveItems(STATUE_FORGERY, 1);
				}
				else
				{
					htmltext = "30929-6.htm";
					st.giveItems(STATUE_PROTOTYPE, 1);
				}
			}
		}
		else if (event.equalsIgnoreCase("30181-6.htm"))
		{
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(true);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(qn);
		String htmltext = getNoQuestMsg();
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case STATE_CREATED:
				htmltext = (player.getLevel() < 36) ? "30181-0a.htm" : "30181-0.htm";
				break;
			
			case STATE_STARTED:
				switch (npc.getNpcId())
				{
					case GALIBREDO:
						htmltext = (st.hasQuestItems(GALIBREDO_BUST)) ? "30181-3a.htm" : "30181-3.htm";
						break;
					
					case PATRIN:
						htmltext = "30929-0.htm";
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		L2PcInstance partyMember = getRandomPartyMemberState(player, npc, STATE_STARTED);
		if (partyMember == null)
			return null;
		
		QuestState st = partyMember.getQuestState(qn);
		
		final int[] chances = CHANCES.get(npc.getNpcId());
		final int random = Rnd.get(100);
		
		if (random < chances[1])
			st.dropItemsAlways((random < chances[0]) ? GALIBREDO_BUST : WORK_OF_BERONA, 1, 0);
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q355_FamilyHonor();
	}
}