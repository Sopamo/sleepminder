package de.sopamo.uni.sleepminder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

public class Hooks {
    public static final int RECORDING_LIST_UPDATE = 0;


    private static HashMap<Integer, ArrayList<Callable<Integer>>> hooks = new HashMap<Integer, ArrayList<Callable<Integer>>>();

    public static void bind(int key, Callable<Integer> callback)
    {
        if(!hooks.containsKey(key))
        {
            hooks.put(key,new ArrayList<Callable<Integer>>());
        }
        hooks.get(key).add(callback);
    }

    public static void clear()
    {
        hooks = new HashMap<Integer, ArrayList<Callable<Integer>>>();
    }

    public static void remove(int key) {
        if(hooks.containsKey(key)) {
            hooks.remove(key);
        }
    }

    public static void call(int key)
    {
        if(!hooks.containsKey(key))
        {
            return;
        }
        List<Callable<Integer>> list = hooks.get(key);
        if(list != null)
        {
            for(int i = 0; i < list.size(); ++i)
            {
                Callable<Integer> callback = list.get(i);
                try
                {
                    callback.call();
                } catch (Exception e)
                {

                }
            }
        }
    }
}