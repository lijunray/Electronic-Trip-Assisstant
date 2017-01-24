package com.nyu.cs9033.eta.jsons;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ray on 2016/4/23.
 */
public interface MessageInterface {

    public JSONObject toJSON () throws JSONException;

}
