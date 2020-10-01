/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2018 Kamax Sàrl
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

package matrix.client.regular;

import com.google.gson.JsonObject;

import matrix.client._Presence;
import matrix.json.GsonUtil;

public class Presence implements _Presence {

    private String status;
    private Long lastActive;

    public Presence(JsonObject json) {
        this.status = GsonUtil.getStringOrThrow(json, "presence");
        this.lastActive = GsonUtil.getLong(json, "last_active_ago");
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public Long getLastActive() {
        return lastActive;
    }

}
