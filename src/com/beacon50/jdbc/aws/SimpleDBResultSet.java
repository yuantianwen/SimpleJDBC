package com.beacon50.jdbc.aws;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.util.SimpleDBUtils;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.ListIterator;

/**
 *
 */
public class SimpleDBResultSet extends AbstractResultSet {

    private List<Item> items;
    private ListIterator<Item> iter;
    private int currentPos = -1;
    @SuppressWarnings("unused")
	private Item currentItem;
    private SimpleDBConnection connection;
    private String domain;


    public ResultSetMetaData getMetaData() throws SQLException {
        return new SimpleDBResultSetMetaData(this.connection, this.items, this.domain);
    }

    protected SimpleDBResultSet(SimpleDBConnection connection, List<Item> items, String domain) {
        this.connection = connection;
        this.items = items;
        this.iter = items.listIterator();
        this.domain = domain;
    }

    public boolean next() throws SQLException {
        if (this.iter.hasNext()) {
            currentPos = this.iter.nextIndex();
            this.currentItem = iter.next();
            return true;
        } else {
            return false;
        }
    }

    public int getInt(int index) throws SQLException {
        checkPosition();

        Item item = items.get(currentPos);
        List<Attribute> attributes = item.getAttributes();
        Attribute attribute = attributes.get((index - 1));
        return SimpleDBUtils.decodeZeroPaddingInt(attribute.getValue());
    }

    private void checkPosition() throws SQLException {
        if (currentPos < 0) {
            throw new SQLException("you must call next() on a ResultSet first!");
        }
    }

    public int getInt(String label) throws SQLException {
        checkPosition();

        Item item = items.get(currentPos);
        List<Attribute> attributes = item.getAttributes();
        for (Attribute attribute : attributes) {
            if (attribute.getName().equals(label)) {
                return SimpleDBUtils.decodeZeroPaddingInt(attribute.getValue());
            }
        }
        throw new SQLException("attribute name " + label + " doesn't exist!");
    }


    public Object getObject(int i) throws SQLException {
        return this.getString(i);
    }


    public Object getObject(String s) throws SQLException {
        return this.getString(s);
    }

    public String getString(int columnIndex) throws SQLException {
        checkPosition();

        Item item = items.get(currentPos);
        List<Attribute> attributes = item.getAttributes();
        Attribute attribute = attributes.get((columnIndex - 1));
        return attribute.getValue();
    }

    public String getString(String columnLabel) throws SQLException {
        checkPosition();

        Item item = items.get(currentPos);
        List<Attribute> attributes = item.getAttributes();
        for (Attribute attribute : attributes) {
            if (attribute.getName().equals(columnLabel)) {
                return attribute.getValue();
            }
        }
        throw new SQLException("attribute name " + columnLabel + " doesn't exist!");
    }

}
