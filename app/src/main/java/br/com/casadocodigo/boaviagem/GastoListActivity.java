package br.com.casadocodigo.boaviagem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import br.com.casadocodigo.boaviagem.dao.GastoDAO;
import br.com.casadocodigo.boaviagem.domain.Gasto;

public class GastoListActivity extends ListActivity implements
		OnItemClickListener {

	private List<Map<String, Object>> gastos;
	private GastoDAO gastoDAO;
	private String viagem_id;
	private SimpleDateFormat dateFormat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.lista_gasto);

		dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

		gastoDAO = new GastoDAO(this);

		viagem_id = getIntent().getStringExtra(Constantes.VIAGEM_ID);

		String[] de = { "data", "descricao", "valor", "categoria" };
		int[] para = { R.id.data, R.id.descricao, R.id.valor, R.id.categoria };

		SimpleAdapter adapter = new SimpleAdapter(this, listarGastos(),
				R.layout.lista_gasto_item, de, para);
		adapter.setViewBinder(new GastoViewBinder());

		setListAdapter(adapter);
		getListView().setOnItemClickListener(this);

		registerForContextMenu(getListView());
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Map<String, Object> map = gastos.get(position);
		Intent intent = new Intent(this, GastoActivity.class);
		intent.putExtra(Constantes.GASTO_ID, (String) map.get("id"));
		intent.putExtra(Constantes.VIAGEM_ID, (String) map.get("viagem_id"));
		startActivity(intent);
	}

	private List<Map<String, Object>> listarGastos() {
		gastos = new ArrayList<Map<String, Object>>();

		Map<String, Object> colors = new HashMap<String, Object>();
		colors.put(getResources().getTextArray(R.array.categoria_gasto)[0]
				.toString(), R.color.categoria_alimentacao);
		colors.put(getResources().getTextArray(R.array.categoria_gasto)[1]
				.toString(), R.color.categoria_combustivel);
		colors.put(getResources().getTextArray(R.array.categoria_gasto)[2]
				.toString(), R.color.categoria_transporte);
		colors.put(getResources().getTextArray(R.array.categoria_gasto)[3]
				.toString(), R.color.categoria_hospedagem);
		colors.put(getResources().getTextArray(R.array.categoria_gasto)[4]
				.toString(), R.color.categoria_outros);

		for (Gasto gasto : gastoDAO.getGastos(viagem_id)) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", gasto.getId().toString());
			item.put("viagem_id", gasto.getViagemId().toString());
			item.put("data", dateFormat.format(gasto.getData()));
			item.put("descricao", gasto.getDescricao());
			item.put("valor", "R$ " + gasto.getValor());

			CharSequence[] categorias = getResources().getTextArray(
					R.array.categoria_gasto);
			for (int i = 0; i < categorias.length; i++) {
				if (categorias[i].toString().equals(gasto.getCategoria())) {
					item.put("categoria", colors.get(categorias[i].toString()));
					break;
				}
			}

			gastos.add(item);
		}

		return gastos;
	};

	private String dataAnterior = "";

	private class GastoViewBinder implements ViewBinder {

		@Override
		public boolean setViewValue(View view, Object data,
				String textRepresentation) {
			if (view.getId() == R.id.data) {
				if (!dataAnterior.equals(data)) {
					TextView textView = (TextView) view;
					textView.setText(textRepresentation);
					dataAnterior = textRepresentation;
					view.setVisibility(View.VISIBLE);
				} else {
					view.setVisibility(View.GONE);
				}
				return true;
			}

			if (view.getId() == R.id.categoria) {
				Integer id = (Integer) data;
				view.setBackgroundColor(getResources().getColor(id));
				return true;
			}
			return false;
		}

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.lista_gasto_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.remover) {
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			Map<String, Object> map = gastos.get(info.position);
			gastoDAO.delete((String) map.get("id"));
			gastos.remove(info.position);
			getListView().invalidateViews();
			dataAnterior = "";
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		gastoDAO.close();
		super.onDestroy();
	}
}
