/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2018 Kamax Sarl
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

import matrix.event._RoomNameEvent;
import matrix.json.MatrixJsonObject;

import java8.util.Optional;

public class MatrixJsonRoomNameEvent extends MatrixJsonRoomEvent implements _RoomNameEvent {

    public static class Content extends MatrixJsonObject {

        private String name;

        public Content(JsonObject obj) {
            super(obj);

            setName(getStringOrNull("name"));
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    protected Content content;

    public MatrixJsonRoomNameEvent(JsonObject obj) {
        super(obj);
        this.content = new Content(getObj("content"));
    }

    @Override
    public Optional<String> getName() {
        return Optional.ofNullable(content.getName());
    }

}
