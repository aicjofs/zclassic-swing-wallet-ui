package com.vaklinov.zcashtest;

import java.io.Writer;
import java.text.DecimalFormat;
import java.util.Date;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.WriterConfig;

public class Test1 
{

	public static void main(String[] args) 
	{
		JsonObject toArgument = new JsonObject();
		toArgument.set("address", "111111111111111111");
		toArgument.set("memo", "222222222222222222222222222");
		toArgument.set("amount", "\uFFFF\uFFFF\uFFFF\uFFFF\uFFFF");
		
		JsonArray toMany = new JsonArray();
		toMany.add(toArgument);

		System.out.println(toMany.toString().
		    replace("\"amount\":\"\uFFFF\uFFFF\uFFFF\uFFFF\uFFFF\"", 
                    "\"amount\":" + new DecimalFormat("#########.00######").format(Double.valueOf("1234567890000"))));
		
		
		final Date startDate = new Date("04 Oct 2016 00:00:00 GMT");
		System.out.println(startDate.toString());
		
	}

}
