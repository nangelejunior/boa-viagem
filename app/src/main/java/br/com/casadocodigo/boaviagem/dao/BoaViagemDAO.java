package br.com.casadocodigo.boaviagem.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class BoaViagemDAO {

	private DatabaseHelper helper;
	private SQLiteDatabase db;

	public BoaViagemDAO(Context context) {
		helper = new DatabaseHelper(context);
	}

	protected SQLiteDatabase getDB() {
		if (db == null) {
			db = helper.getWritableDatabase();
		}
		return db;
	}

	public void close() {
		if (db != null && db.isOpen()) {
			db.close();
		}

		if (helper != null) {
			helper.close();
		}
	}
}
