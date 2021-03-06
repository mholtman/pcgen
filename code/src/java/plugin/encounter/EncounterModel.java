/*
 * Copyright 2003 (C) Devon Jones
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */
 package plugin.encounter;

import gmgen.io.ReadXML;

import java.io.File;
import java.lang.reflect.Array;

import javax.swing.DefaultListModel;

import pcgen.cdom.content.ChallengeRating;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;

/**
 * This {@code class} holds all the necessary data in order to have
 * functionality for the Encounter Generator.<br>
 * Created on February 19, 2003<br>
 * Updated on March 12, 2003
 * 
 * @author John Dells &lt;JohnDells@woh.rr.com&gt;
 */
public class EncounterModel extends DefaultListModel
{
	private String dir;

	/** All the characters or creatures in combat. */
	private PlayerCharacter[] PCs;

	/**
	 * Creates a new instance of EncounterModel
	 * @param parentDir
	 */
	public EncounterModel(String parentDir)
	{
		dir = parentDir;
	}

	/**
	 * Gets the challenge rating of the group of characters.
	 * @return the challenge rating.
	 */
	public int getCR()
	{
		ReadXML xml;
		File f = new File(dir + File.separator + "4_1.xml"); //$NON-NLS-1$
		int i;
		float cr = 0;

		xml = new ReadXML(f);
		xml.getTable();

		for (i = 0; i < size(); i++)
		{
			Race aRace = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Race.class, (String) elementAt(i));
			ChallengeRating rcr = aRace.get(ObjectKey.CHALLENGE_RATING);
			if (rcr != null)
			{
				/*
				 * CrLst enforces a certain structure x or 1/x where x is an integer,
				 * so we KNOW this is a fixed value.  We skip the isStatic() test.
				 */
				cr += mCRtoPL(rcr.getRating().resolveStatic().floatValue());
			}
		}

		cr = mPLtoCR(cr);

		if (cr < 0)
		{
			cr = 0;
		}

		return (int) (cr + 0.5);
	}

	/**
	 * Sets the {@code Array} of <code>PlayerCharacters</code>.
	 * @param len the number of characters being created.
	 */
	public void setPCs(int len)
	{
		int x;
		PCs = (PlayerCharacter[]) Array.newInstance(PlayerCharacter.class, len);

		for (x = 0; x < len; x++)
		{
			PCs[x] = new PlayerCharacter();
		}
	}

	/**
	 * Gets all the characters in the encounter.
	 * @return the {@code Array} of characters.
	 */
	public PlayerCharacter[] getPCs()
	{
		return PCs;
	}

	/**
	 * Takes the CR of a monster and transforms it into "power level", used when summing monsters for total CR
	 * @param x
	 * @return "power level"
	 */
	public float mCRtoPL(float x)
	{
		float iReturn = 0;

		if (x < 1)
		{
			iReturn = x;
		}
		else
		{
			iReturn = (float) Math.exp((x - 1) / 2);
		}

		return iReturn;
	}

	/**
	 * Takes a "power level" into CR. See {@code mCRtoPL()} for details.
	 * @param x
	 * @return "power level"
	 */
	public int mPLtoCR(float x)
	{
		return (int) ((2 * Math.log(x)) + 1);
	}
}
