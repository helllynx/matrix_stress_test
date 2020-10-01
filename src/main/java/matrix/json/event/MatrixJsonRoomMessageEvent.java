/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2017 Kamax Sarl
 *
 * https://www.kamax.io/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package matrix.json.event;

import com.google.gson.JsonObject;

import matrix.event._RoomMessageEvent;
import matrix.json.GsonUtil;

import java8.util.Optional;

public class MatrixJsonRoomMessageEvent extends MatrixJsonRoomEvent implements _RoomMessageEvent {

    protected JsonObject content;

    public MatrixJsonRoomMessageEvent(JsonObject obj) {
        super(obj);

        this.content = obj.getAsJsonObject("content");
    }

    @Override
    public String getBody() {
        return content.get("body").getAsString();
    }

    @Override
    public String getBodyType() {
        return content.has("msgtype") ? content.get("msgtype").getAsString() : null;
    }

    @Override
    public Optional<String> getFormat() {
        return GsonUtil.findString(content, "format");
    }

    @Override
    public Optional<String> getFormattedBody() {
        return GsonUtil.findString(content, "formatted_body");
    }

}
