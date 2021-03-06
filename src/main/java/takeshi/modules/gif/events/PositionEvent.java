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

package takeshi.modules.gif.events;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import takeshi.modules.gif.OverlayActor;
import takeshi.modules.gif.OverlayEvent;

/**
 * The type Position event.
 */
public class PositionEvent implements OverlayEvent {
    private static final Pattern inputPattern = Pattern.compile("pos:? ?(\\d+) (\\d+)", Pattern.CASE_INSENSITIVE);
    private final int x;
    private final int y;

    private PositionEvent(int x, int y) {

        this.x = x;
        this.y = y;
    }

    @Override
    public String getTrigger() {
        return "pos";
    }

    @Override
    public boolean isValid(String input) {
        return inputPattern.matcher(input).find();
    }

    @Override
    public OverlayEvent parse(String input) {
        Matcher matcher = inputPattern.matcher(input);
        if (matcher.find()) {
            return new PositionEvent(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
        }
        return null;
    }

    @Override
    public void apply(OverlayActor overlayActor) {
        overlayActor.x = x;
        overlayActor.y = y;
    }
}
