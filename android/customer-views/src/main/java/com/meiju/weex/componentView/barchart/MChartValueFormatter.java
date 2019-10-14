package com.meiju.weex.componentView.barchart;

import android.text.TextUtils;
import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XIONGQI on 2018-4-17.
 */

public class MChartValueFormatter {
    public static class MyAxisValueFormatter extends IndexAxisValueFormatter {

        public MyAxisValueFormatter(List<String> values) {
            super(values);
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            int index = Math.round(value);

            if (index < 0 || index >= getValues().length || index != (int)value){
                Log.i("MyXAxisValueFormatter", String.format("x轴值%f 当前序号：%d 返回%s",value,index,"空"));
                return "";
            }

            Log.i("MyXAxisValueFormatter", String.format("x轴值%f 当前序号：%d 返回%s",value,index,getValues()[index]));
            return getValues()[index];
        }
    }

    public static class MyValueFormatter implements IValueFormatter {

        private Map<Integer, String> mValues = new HashMap<>();
        private int mValueCount = 0;

        public MyValueFormatter(List<Integer> keys, List<String> values) {
            if (keys!=null && values != null)
                setValues(keys, values);
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            String showValue = mValues.get(entry.hashCode());
            if(TextUtils.isEmpty(showValue))
                showValue = "";
            Log.i("MyValueFormatter", String.format("value值%f ,返回%s",value,showValue));
            return showValue;
        }

        private void setValues(List<Integer> keys, List<String> values) {
            if (values == null)
                values = new ArrayList<>();
            if (keys == null)
                keys = new ArrayList<>();

            if(keys.size() != values.size())
                this.mValueCount = Math.min(keys.size(), values.size());
            else
                this.mValueCount = keys.size();
            for (int i = 0; i <mValueCount; i++) {
                mValues.put(keys.get(i), values.get(i));
            }
        }
    }
}
