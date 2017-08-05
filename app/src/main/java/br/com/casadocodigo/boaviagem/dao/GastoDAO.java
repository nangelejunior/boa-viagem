package br.com.casadocodigo.boaviagem.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import br.com.casadocodigo.boaviagem.domain.Gasto;

public class GastoDAO extends BoaViagemDAO {

	public GastoDAO(Context context) {
		super(context);
	}

	public double calcularTotalGasto(String id) {
		double total = 0.0d;

		Cursor cursor = getDB().rawQuery(
				"SELECT sum(valor) FROM gasto WHERE viagem_id = ?",
				new String[] { id });

		cursor.moveToFirst();

		total = cursor.getDouble(0);

		if (!cursor.isClosed())
			cursor.close();

		return total;
	}

	public Gasto getGasto(String id) {
		Gasto retorno = null;

		Cursor cursor = getDB().query(DatabaseHelper.Gasto.TABELA,
				DatabaseHelper.Gasto.COLUNAS, "_id = ?", new String[] { id },
				null, null, null);

		while (cursor.moveToNext()) {
			retorno = criarGasto(cursor);
		}

		if (!cursor.isClosed())
			cursor.close();

		return retorno;
	}

	public List<Gasto> getGastos(String viagem_id) {
		List<Gasto> retorno = new ArrayList<Gasto>();

		Cursor cursor = getDB().query(DatabaseHelper.Gasto.TABELA,
				DatabaseHelper.Gasto.COLUNAS, "viagem_id = ?",
				new String[] { viagem_id }, null, null,
				DatabaseHelper.Gasto.DATA);

		while (cursor.moveToNext()) {
			retorno.add(criarGasto(cursor));
		}

		if (!cursor.isClosed())
			cursor.close();

		return retorno;
	}

	private Gasto criarGasto(Cursor cursor) {
		Gasto gasto = new Gasto();
		gasto.setId(cursor.getLong(cursor
				.getColumnIndex(DatabaseHelper.Gasto._ID)));
		gasto.setViagemId(cursor.getInt(cursor
				.getColumnIndex(DatabaseHelper.Gasto.VIAGEM_ID)));
		gasto.setCategoria(cursor.getString(cursor
				.getColumnIndex(DatabaseHelper.Gasto.CATEGARIA)));
		gasto.setValor(cursor.getDouble(cursor
				.getColumnIndex(DatabaseHelper.Gasto.VALOR)));
		gasto.setData(new Date(cursor.getLong(cursor
				.getColumnIndex(DatabaseHelper.Gasto.DATA))));
		gasto.setDescricao(cursor.getString(cursor
				.getColumnIndex(DatabaseHelper.Gasto.DESCRICAO)));
		gasto.setLocal(cursor.getString(cursor
				.getColumnIndex(DatabaseHelper.Gasto.LOCAL)));

		return gasto;
	}

	public long insert(Gasto gasto) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.Gasto.VIAGEM_ID, gasto.getViagemId());
		values.put(DatabaseHelper.Gasto.CATEGARIA, gasto.getCategoria());
		values.put(DatabaseHelper.Gasto.VALOR, gasto.getValor());
		values.put(DatabaseHelper.Gasto.DATA, gasto.getData().getTime());
		values.put(DatabaseHelper.Gasto.DESCRICAO, gasto.getDescricao());
		values.put(DatabaseHelper.Gasto.LOCAL, gasto.getLocal());

		return getDB().insert(DatabaseHelper.Gasto.TABELA, null, values);
	}

	public int update(Gasto gasto) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.Gasto.VIAGEM_ID, gasto.getViagemId());
		values.put(DatabaseHelper.Gasto.CATEGARIA, gasto.getCategoria());
		values.put(DatabaseHelper.Gasto.VALOR, gasto.getValor());
		values.put(DatabaseHelper.Gasto.DATA, gasto.getData().getTime());
		values.put(DatabaseHelper.Gasto.DESCRICAO, gasto.getDescricao());
		values.put(DatabaseHelper.Gasto.LOCAL, gasto.getLocal());

		return getDB().update(DatabaseHelper.Gasto.TABELA, values, "_id = ?",
				new String[] { gasto.getId().toString() });
	}

	public boolean delete(String id) {
		return getDB().delete(DatabaseHelper.Gasto.TABELA, "_id = ?",
				new String[] { id }) > 0;
	}
}
