package br.com.casadocodigo.boaviagem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import br.com.casadocodigo.boaviagem.dao.GastoDAO;
import br.com.casadocodigo.boaviagem.domain.Gasto;

public class GastoActivity extends Activity {

	private Calendar dataGasto;
	private SimpleDateFormat dateFormat;
	private Spinner categoria;
	private Button dataGastoButton;
	private EditText valor, descricao, local;
	private GastoDAO gastoDAO;
	private String id;
	private String viagem_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gasto);

		dataGasto = Calendar.getInstance(Locale.getDefault());

		dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.categoria_gasto,
				android.R.layout.simple_spinner_item);

		categoria = (Spinner) findViewById(R.id.categoria);
		categoria.setAdapter(adapter);

		dataGastoButton = (Button) findViewById(R.id.data);
		dataGastoButton.setText(dateFormat.format(dataGasto.getTime()));

		valor = (EditText) findViewById(R.id.valor);
		descricao = (EditText) findViewById(R.id.descricao);
		local = (EditText) findViewById(R.id.local);

		gastoDAO = new GastoDAO(this);

		id = getIntent().getStringExtra(Constantes.GASTO_ID);

		if (id != null) {
			prepararEdicao();
		}

		viagem_id = getIntent().getStringExtra(Constantes.VIAGEM_ID);
	}

	public void prepararEdicao() {
		Gasto gasto = gastoDAO.getGasto(id);

		if (gasto != null) {
			viagem_id = gasto.getViagemId().toString();
			CharSequence[] categorias = getResources().getTextArray(
					R.array.categoria_gasto);
			for (int i = 0; i < categorias.length; i++) {
				if (categorias[i].toString().equals(gasto.getCategoria())) {
					categoria.setSelection(i);
					break;
				}
			}
			valor.setText(gasto.getValor().toString());
			dataGastoButton.setText(dateFormat.format(gasto.getData()));
			descricao.setText(gasto.getDescricao());
			local.setText(gasto.getLocal());
		}
	}

	public void selecionarData(View view) {
		showDialog(view.getId());
	}

	@Override
	protected Dialog onCreateDialog(int i) {
		if (R.id.data == i) {
			return new DatePickerDialog(this, listener,
					dataGasto.get(Calendar.YEAR),
					dataGasto.get(Calendar.MONTH),
					dataGasto.get(Calendar.DAY_OF_MONTH));
		}
		return null;
	}

	private OnDateSetListener listener = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			dataGasto.set(year, monthOfYear, dayOfMonth);
			dataGastoButton.setText(dateFormat.format(dataGasto.getTime()));
		}
	};

	public void registrarGasto(View view) {
		boolean isValido = true;

		if ("".equals(valor.getText().toString().trim())) {
			valor.setError(getString(R.string.campo_obrigatorio));
			isValido = false;
		}

		if ("".equals(descricao.getText().toString().trim())) {
			descricao.setError(getString(R.string.campo_obrigatorio));
			isValido = false;
		}

		if ("".equals(local.getText().toString().trim())) {
			local.setError(getString(R.string.campo_obrigatorio));
			isValido = false;
		}

		if (isValido) {
			Gasto gasto = new Gasto();
			gasto.setId(id == null ? -1 : Long.valueOf(id));
			gasto.setViagemId(viagem_id == null ? -1 : Integer
					.valueOf(viagem_id));
			gasto.setCategoria(categoria.getSelectedItem().toString().trim());
			gasto.setValor(Double.valueOf(valor.getText().toString().trim()));
			gasto.setData(dataGasto.getTime());
			gasto.setDescricao(descricao.getText().toString().trim());
			gasto.setLocal(local.getText().toString().trim());

			long resultado;
			if (id == null) {
				resultado = gastoDAO.insert(gasto);
				id = String.valueOf(resultado);
			} else {
				resultado = gastoDAO.update(gasto);
			}

			if (resultado > 0) {
				Toast.makeText(this, getString(R.string.registro_salvo),
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, getString(R.string.erro_salvar),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public boolean onCreatePanelMenu(int featureId, Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.gasto_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.nova_viagem:
			startActivity(new Intent(this, ViagemActivity.class));
			return true;
		case R.id.remover:
			return gastoDAO.delete(id);
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	@Override
	protected void onDestroy() {
		gastoDAO.close();
		super.onDestroy();
	}
}
