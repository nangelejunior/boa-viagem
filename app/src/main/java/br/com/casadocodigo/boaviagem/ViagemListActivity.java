package br.com.casadocodigo.boaviagem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import br.com.casadocodigo.boaviagem.dao.GastoDAO;
import br.com.casadocodigo.boaviagem.dao.ViagemDAO;
import br.com.casadocodigo.boaviagem.domain.Viagem;

public class ViagemListActivity extends ListActivity implements
		OnItemClickListener, OnClickListener {

	private List<Map<String, Object>> viagens;
	private AlertDialog dialogConfirmacao;
	private AlertDialog alertDialog;
	private int viagemSelecionada;
	private ViagemDAO viagemDAO;
	private SimpleDateFormat dateFormat;
	private Double valorLimite;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.lista_viagem);

		viagemDAO = new ViagemDAO(this);
		dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

		SharedPreferences preferencias = PreferenceManager
				.getDefaultSharedPreferences(this);

		String valor = preferencias.getString("valor_limite", "-1");
		valorLimite = Double.valueOf(valor);

		String[] de = { "imagem", "destino", "data", "total", "barraProgresso" };
		int[] para = { R.id.tipoViagem, R.id.destino, R.id.data, R.id.valor,
				R.id.barraProgresso };

		SimpleAdapter adapter = new SimpleAdapter(this, listarViagens(),
				R.layout.lista_viagem_item, de, para);

		adapter.setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				if (view.getId() == R.id.barraProgresso) {
					Double[] valores = (Double[]) data;
					ProgressBar progressBar = (ProgressBar) view;
					progressBar.setMax(valores[0].intValue());
					progressBar.setSecondaryProgress(valores[1].intValue());
					progressBar.setProgress(valores[2].intValue());
					return true;
				}
				return false;
			}
		});

		setListAdapter(adapter);

		getListView().setOnItemClickListener(this);

		this.alertDialog = criaAlertDialog();
		this.dialogConfirmacao = criaDialogConfirmacao();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		this.viagemSelecionada = position;
		alertDialog.show();
	}

	private List<Map<String, Object>> listarViagens() {
		viagens = new ArrayList<Map<String, Object>>();

		for (Viagem viagem : viagemDAO.getViagens()) {
			Map<String, Object> item = new HashMap<String, Object>();

			item.put("id", viagem.getId().toString());

			if (viagem.getTipoViagem() == Constantes.VIAGEM_LAZER) {
				item.put("imagem", R.drawable.lazer);
			} else {
				item.put("imagem", R.drawable.negocios);
			}

			item.put("destino", viagem.getDestino());

			String periodo = dateFormat.format(viagem.getDataChegada()) + " a "
					+ dateFormat.format(viagem.getDataSaida());

			item.put("data", periodo);

			double totalGasto = new GastoDAO(this).calcularTotalGasto(viagem.getId()
					.toString());

			item.put("total", String.format("Gasto total R$ %.2f", totalGasto));

			double alerta = viagem.getOrcamento() * valorLimite / 100;
			Double[] valores = new Double[] { viagem.getOrcamento(), alerta,
					totalGasto };

			item.put("barraProgresso", valores);

			viagens.add(item);
		}

		return viagens;
	}

	private AlertDialog criaAlertDialog() {
		final CharSequence[] items = { getString(R.string.editar),
				getString(R.string.novo_gasto),
				getString(R.string.gastos_realizados),
				getString(R.string.remover) };

		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(R.string.opcoes);
		builder.setItems(items, this);

		return builder.create();
	};

	@Override
	public void onClick(DialogInterface dialog, int which) {
		Intent intent;
		String id = (String) viagens.get(viagemSelecionada).get("id");

		switch (which) {
		case 0:
			intent = new Intent(this, ViagemActivity.class);
			intent.putExtra(Constantes.VIAGEM_ID, id);
			startActivity(intent);
			break;
		case 1:
			intent = new Intent(this, GastoActivity.class);
			intent.putExtra(Constantes.VIAGEM_ID, id);
			startActivity(intent);
			break;
		case 2:
			intent = new Intent(this, GastoListActivity.class);
			intent.putExtra(Constantes.VIAGEM_ID, id);
			startActivity(intent);
			break;
		case 3:
			dialogConfirmacao.show();
			break;
		case DialogInterface.BUTTON_POSITIVE:
			viagemDAO.delete(id);
			viagens.remove(this.viagemSelecionada);
			getListView().invalidateViews();
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			dialogConfirmacao.dismiss();
			break;
		}
	}

	private AlertDialog criaDialogConfirmacao() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.confirmacao_exclusao_viagem);
		builder.setPositiveButton(getString(R.string.sim), this);
		builder.setNegativeButton(getString(R.string.nao), this);

		return builder.create();
	}

	@Override
	protected void onDestroy() {
		viagemDAO.close();
		super.onDestroy();
	}
}
