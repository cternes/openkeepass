package de.slackspace.openkeepass.domain.xml.adapter;

import java.util.Map;

import org.simpleframework.xml.strategy.TreeStrategy;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.NodeMap;

public class TreeStrategyWithoutArrayLength extends TreeStrategy {

    @SuppressWarnings("rawtypes")
    @Override
    public boolean write(Type type, Object value, NodeMap node, Map map){
        Class actual = value.getClass();
        Class expect = type.getType();
        Class real = actual;
        
        if(actual != expect) {
           node.put("class", real.getName());
        }       
        return false;
     }
}
