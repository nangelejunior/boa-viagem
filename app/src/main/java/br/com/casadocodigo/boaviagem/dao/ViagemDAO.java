package br.com.casadocodigo.boaviagem.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import br.com.casadocodigo.boaviagem.domain.Viagem;

public class ViagemDAO extends BoaViagemDAO {

	public ViagemDAO(Context context) {
		super(context);
	}

	public Viagem getViagem(String id) {
		Viagem retorno = null;

		Cursor cursor = getDB().query(DatabaseHelper.Viagem.TABELA,
				DatabaseHelper.Viagem.COLUNAS, "_id = ?", new String[] { id },
				null, null, null);

		while (cursor.moveToNext()) {
			retorno = criarViagem(cursor);
		}

		if (!cursor.isClosed())
			cursor.close();

		return retorno;
	}

	public List<Viagem> getViagens() {
		List<Viagem> retorno = new ArrayList<Viagem>();

		Cursor cursor = getDB().query(DatabaseHelper.Viagem.TABELA,
				DatabaseHelper.Viagem.COLUNAS, null, null, null, null, null);

		while (cursor.moveToNext()) {
			retorno.add(criarViagem(cursor));
		}

		if (!cursor.isClosed())
			cursor.close();

		return retorno;
	}

	private Viagem criarViagem(Cursor cursor) {
		Viagem viagem = new Viagem();
		viagem.setId(cursor.getLong(cursor
				.getColumnIndex(DatabaseHelper.Viagem._ID)));
		viagem.setDestino(cursor.getString(cursor
				.getColumnIndex(DatabaseHelper.Viagem.DESTINO)));
		viagem.setDataChegada(new Date(cursor.getLong(cursor
				.getColumnIndex(DatabaseHelper.Viagem.DATA_CHEGADA))));
		viagem.setDataSaida(new Date(cursor.getLong(cursor
				.getColumnIndex(DatabaseHelper.Viagem.DATA_SAIDA))));
		viagem.setOrcamento(cursor.getDouble(cursor
				.getColumnIndex(DatabaseHelper.Viagem.ORCAMENTO)));
		viagem.setQuantidadePessoas(cursor.getInt(cursor
				.getColumnIndex(DatabaseHelper.Viagem.QUANTIDADE_PESSOAS)));
		viagem.setTipoViagem(cursor.getInt(cursor
				.getColumnIndex(DatabaseHelper.Viagem.TIPO_VIAGEM)));

		return viagem;
	}

	public long insert(Viagem viagem) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.Viagem.DESTINO, viagem.getDestino());
		values.put(DatabaseHelper.Viagem.DATA_CHEGADA, viagem.getDataChegada()
				.getTime());
		values.put(DatabaseHelper.Viagem.DATA_SAIDA, viagem.getDataSaida()
				.getTime());
		values.put(DatabaseHelper.Viagem.ORCAMENTO, viagem.getOrcamento()
				.toString());
		values.put(DatabaseHelper.Viagem.QUANTIDADE_PESSOAS, viagem
				.getQuantidadePessoas().toString());
		values.put(DatabaseHelper.Viagem.TIPO_VIAGEM, viagem.getTipoViagem());

		return getDB().insert(DatabaseHelper.Viagem.TABELA, null, values);
	}

	public int update(Viagem viagem) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.Viagem.DESTINO, viagem.getDestino());
		values.put(DatabaseHelper.Viagem.DATA_CHEGADA, viagem.getDataChegada()
				.getTime());
		values.put(DatabaseHelper.Viagem.DATA_SAIDA, viagem.getDataSaida()
				.getTime());
		values.put(DatabaseHelper.Viagem.ORCAMENTO, viagem.getOrcamento()
				.toString());
		values.put(DatabaseHelper.Viagem.QUANTIDADE_PESSOAS, viagem
				.getQuantidadePessoas().toString());
		values.put(DatabaseHelper.Viagem.TIPO_VIAGEM, viagem.getTipoViagem());

		return getDB().update(DatabaseHelper.Viagem.TABELA, values, "_id = ?",
				new String[] { viagem.getId().toString() });
	}

	public boolean delete(String id) {
		String where[] = new String[] { id };
		getDB().delete(DatabaseHelper.Gasto.TABELA, "viagem_id = ?", where);
		return getDB().delete(DatabaseHelper.Viagem.TABELA, "_id = ?", where) > 0;
	}
}
