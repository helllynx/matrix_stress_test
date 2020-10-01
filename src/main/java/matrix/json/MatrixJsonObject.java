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

package matrix.json;

import com.google.gson.*;

import java8.util.Objects;
import java8.util.Optional;

public class MatrixJsonObject {

    private JsonObject obj;

    public MatrixJsonObject(JsonObject obj) {
        if (Objects.isNull(obj)) {
            throw new InvalidJsonException("JSON Object is null");
        }

        this.obj = obj;
    }

    protected Optional<String> findString(String field) {
        return GsonUtil.findString(obj, field);
    }

    protected String getString(String field) {
        return GsonUtil.getStringOrNull(obj, field);
    }

    protected String getStringOrNull(JsonObject obj, String field) {
        return GsonUtil.findString(obj, field).orElse(null);
    }

    protected String getStringOrNull(String field) {
        return getStringOrNull(obj, field);
    }

    protected int getInt(String field) {
        return GsonUtil.getPrimitive(obj, field).getAsInt();
    }

    protected int getInt(String field, int failover) {
        return GsonUtil.findPrimitive(obj, field).map(JsonPrimitive::getAsInt).orElse(failover);
    }

    protected Optional<Long> findLong(String field) {
        return GsonUtil.findLong(obj, field);
    }

    protected long getLong(String field) {
        return GsonUtil.getLong(obj, field);
    }

    /*
     * Returns the Double value, if the key is present, null else
     */
    protected Double getDoubleIfPresent(String field) {
        if (obj.get(field) != null) {
            return GsonUtil.getPrimitive(obj, field).getAsDouble();
        }
        return null;
    }

    protected JsonObject asObj(JsonElement el) {
        if (!el.isJsonObject()) {
            throw new IllegalArgumentException("Not a JSON object");
        }

        return el.getAsJsonObject();
    }

    protected JsonObject getObj(String field) {
        return GsonUtil.getObj(obj, field);
    }

    protected Optional<JsonObject> findObj(String field) {
        return GsonUtil.findObj(obj, field);
    }

    protected JsonObject computeObj(String field) {
        return findObj(field).orElseGet(JsonObject::new);
    }

    protected Optional<JsonArray> findArray(String field) {
        return GsonUtil.findArray(obj, field);
    }

    public JsonObject getJson() {
        return obj;
    }

}
