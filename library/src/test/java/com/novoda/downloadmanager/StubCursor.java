package com.novoda.downloadmanager;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class StubCursor implements Cursor {

    private final List<String> columnNames;
    private final Map<String, List<String>> rowsByColumn;

    private int position = 0;

    private StubCursor(List<String> columnNames, Map<String, List<String>> rowsByColumn) {
        this.columnNames = columnNames;
        this.rowsByColumn = rowsByColumn;
    }

    @Override
    public int getCount() {
        String firstColumn = columnNames.get(0);
        return rowsByColumn.get(firstColumn).size();
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public boolean move(int i) {
        int moveTo = position + i;
        if (moveTo > getCount()) {
            return false;
        }

        position = moveTo;
        return true;
    }

    @Override
    public boolean moveToPosition(int i) {
        position = i;
        return true;
    }

    @Override
    public boolean moveToFirst() {
        position = 0;
        return true;
    }

    @Override
    public boolean moveToLast() {
        String firstColumn = columnNames.get(0);
        position = rowsByColumn.get(firstColumn).size() - 1;
        return true;
    }

    @Override
    public boolean moveToNext() {
        position++;
        return true;
    }

    @Override
    public boolean moveToPrevious() {
        position--;
        return true;
    }

    @Override
    public boolean isFirst() {
        return position == 0;
    }

    @Override
    public boolean isLast() {
        String firstColumn = columnNames.get(0);
        return position == rowsByColumn.get(firstColumn).size() - 1;
    }

    @Override
    public boolean isBeforeFirst() {
        return position < 0;
    }

    @Override
    public boolean isAfterLast() {
        String firstColumn = columnNames.get(0);
        return position >= rowsByColumn.get(firstColumn).size() - 1;
    }

    @Override
    public int getColumnIndex(String s) {
        for (int i = 0; i < columnNames.size(); i++) {
            if (columnNames.get(i).equals(s)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getColumnIndexOrThrow(String s) throws IllegalArgumentException {
        int columnIndex = getColumnIndex(s);
        if (columnIndex == -1) {
            throw new IllegalArgumentException("Could not find index of column with: " + s);
        }
        return columnIndex;
    }

    @Override
    public String getColumnName(int i) {
        return columnNames.get(i);
    }

    @Override
    public String[] getColumnNames() {
        return columnNames.toArray(new String[columnNames.size()]);
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Override
    public byte[] getBlob(int i) {
        return new byte[0];
    }

    @Override
    public String getString(int i) {
        String columnName = columnNames.get(i);
        List<String> rowsInColumn = rowsByColumn.get(columnName);
        return rowsInColumn.get(position);
    }

    @Override
    public void copyStringToBuffer(int i, CharArrayBuffer charArrayBuffer) {

    }

    @Override
    public short getShort(int i) {
        return 0;
    }

    @Override
    public int getInt(int i) {
        return 0;
    }

    @Override
    public long getLong(int i) {
        return 0;
    }

    @Override
    public float getFloat(int i) {
        return 0;
    }

    @Override
    public double getDouble(int i) {
        return 0;
    }

    @Override
    public int getType(int i) {
        return 0;
    }

    @Override
    public boolean isNull(int i) {
        return false;
    }

    @Override
    public void deactivate() {

    }

    @Override
    public boolean requery() {
        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void registerContentObserver(ContentObserver contentObserver) {

    }

    @Override
    public void unregisterContentObserver(ContentObserver contentObserver) {

    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void setNotificationUri(ContentResolver contentResolver, Uri uri) {

    }

    @Override
    public Uri getNotificationUri() {
        return null;
    }

    @Override
    public boolean getWantsAllOnMoveCalls() {
        return false;
    }

    @Override
    public void setExtras(Bundle bundle) {

    }

    @Override
    public Bundle getExtras() {
        return null;
    }

    @Override
    public Bundle respond(Bundle bundle) {
        return null;
    }

    static class Builder {

        private List<String> columns = new ArrayList<>();
        private Map<String, List<String>> rowsByColumn = new HashMap<>();

        Builder withRowValues(String column, String rowValue, String... rowValues) {
            columns.add(column);

            List<String> copyRowValues = new ArrayList<>();
            copyRowValues.add(rowValue);
            copyRowValues.addAll(Arrays.asList(rowValues));
            rowsByColumn.put(column, copyRowValues);
            return this;
        }

        StubCursor build() {
            return new StubCursor(columns, rowsByColumn);
        }

    }
}