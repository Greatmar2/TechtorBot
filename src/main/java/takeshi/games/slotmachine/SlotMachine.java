/*
 * Copyright 2017 github.com/kaaz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package takeshi.games.slotmachine;

import java.util.Random;

/**
 * The type Slot machine.
 */
public class SlotMachine {
	/**
	 * The constant emptySLotIcon.
	 */
	public final static String emptySLotIcon = ":white_small_square:";
    private final Random rng;
    private final Slot[] slotOptions = Slot.values();
    private final int wheels;
    private final int[] results;
    private int currentWheel;
    private Slot winSlot = slotOptions[0];
    private int winSlotTimes = 0;
    private int winMultiplier = 0;

	/**
	 * Instantiates a new Slot machine.
	 */
	public SlotMachine() {
        rng = new Random();
        wheels = 3;
        currentWheel = 0;
        results = new int[wheels];
    }

	/**
	 * Spin.
	 */
	public void spin() {
        if (currentWheel < wheels) {
            results[currentWheel] = rng.nextInt(slotOptions.length) + slotOptions.length;
            currentWheel++;
        }
        if (!gameInProgress()) {
            calculateWinnings();
        }
    }

    private void calculateWinnings() {
        if (results[0] == results[1] && results[1] == results[2]) {
            winSlot = slotOptions[results[0] % slotOptions.length];
            winMultiplier = slotOptions[results[0] % slotOptions.length].getTriplePayout();
            winSlotTimes = 3;
        } else if ((results[0] == results[1] || results[0] == results[2]) && slotOptions[results[0] % slotOptions.length].getDoublePayout() > 0) {
            winSlot = slotOptions[results[0] % slotOptions.length];
            winMultiplier = slotOptions[results[0] % slotOptions.length].getDoublePayout();
            winSlotTimes = 2;
        } else if (results[1] == results[2] && slotOptions[results[1] % slotOptions.length].getDoublePayout() > 0) {
            winSlot = slotOptions[results[1] % slotOptions.length];
            winMultiplier = slotOptions[results[1] % slotOptions.length].getDoublePayout();
            winSlotTimes = 2;
        } else {
            for (int result : results) {
                if (slotOptions[result % slotOptions.length].getSinglePayout() > 0) {
                    winSlot = slotOptions[result % slotOptions.length];
                    winMultiplier = slotOptions[result % slotOptions.length].getSinglePayout();
                    winSlotTimes = 1;
                    break;
                }
            }
        }
    }

	/**
	 * Gets win slot.
	 *
	 * @return the win slot
	 */
	public Slot getWinSlot() {
        return winSlot;
    }

	/**
	 * Gets win multiplier.
	 *
	 * @return the win multiplier
	 */
	public int getWinMultiplier() {
        return winMultiplier;
    }

	/**
	 * Game in progress boolean.
	 *
	 * @return the boolean
	 */
	public boolean gameInProgress() {
        return wheels > currentWheel;
    }

    private String getIconForIndex(int i) {
        if (i <= 0) {
            return emptySLotIcon;
        }
        return slotOptions[i % slotOptions.length].getEmote();
    }

    @Override
    public String toString() {
        StringBuilder table = new StringBuilder("the slotmachine!\n");
        String[] machineLine = new String[wheels];
        for (int i = 0; i < wheels; i++) {
            machineLine[i] = "";
        }
        int totalrows = 3;
        for (int col = 0; col < wheels; col++) {
            int offset = -1;
            for (int row = 0; row < totalrows; row++) {
                if (results[col] > 0) {
                    machineLine[row] += "|" + getIconForIndex(offset + results[col]);
                } else {
                    machineLine[row] += "|" + getIconForIndex(results[col]);
                }
                offset++;
            }

        }
        for (int i = 0; i < wheels; i++) {
            table.append(machineLine[i]).append("|\n");
        }
        return table.toString();
    }

	/**
	 * Gets win slot times.
	 *
	 * @return the win slot times
	 */
	public int getWinSlotTimes() {
        return winSlotTimes;
    }
}
