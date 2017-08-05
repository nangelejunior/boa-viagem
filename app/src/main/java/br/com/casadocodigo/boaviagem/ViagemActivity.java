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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import br.com.casadocodigo.boaviagem.dao.ViagemDAO;
import br.com.casadocodigo.boaviagem.domain.Viagem;

public class ViagemActivity extends Activity {

	private Calendar dataSaida;
	private Calendar dataChegada;
	private SimpleDateFormat dateFormat;
	private Button dataSaidaButton;
	private Button dataChegadaButton;
	private EditText destino, quantidadePessoas, orcamento;
	private RadioGroup radioGroup;
	private ViagemDAO viagemDAO;
	private String id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viagem);

		dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

		dataSaida = Calendar.getInstance(Locale.getDefault());
		dataChegada = Calendar.getInstance(Locale.getDefault());

		dataSaidaButton = (Button) findViewById(R.id.dataSaida);
		dataSaidaButton.setText(dateFormat.format(dataSaida.getTime()));

		dataChegadaButton = (Button) findViewById(R.id.dataChegada);
		dataChegadaButton.setText(dateFormat.format(dataChegada.getTime()));

		radioGroup = (RadioGroup) findViewById(R.id.tipoViagem);

		destino = (EditText) findViewById(R.id.destino);
		quantidadePessoas = (EditText) findViewById(R.id.quantidadePessoas);
		orcamento = (EditText) findViewById(R.id.orcamento);

		viagemDAO = new ViagemDAO(this);

		id = getIntent().getStringExtra(Constantes.VIAGEM_ID);

		if (id != null) {
			prepararEdicao();
		}
	}

	private void prepararEdicao() {
		Viagem viagem = viagemDAO.getViagem(id);

		if (viagem != null) {
			if (viagem.getTipoViagem() == Constantes.VIAGEM_LAZER) {
				radioGroup.check(R.id.lazer);
			} else {
				radioGroup.check(R.id.negacios);
			}

			destino.setText(viagem.getDestino());
			dataSaida.setTime(viagem.getDataSaida());
			dataChegada.setTime(viagem.getDataChegada());
			dataSaidaButton.setText(dateFormat.format(dataSaida.getTime()));
			dataChegadaButton.setText(dateFormat.format(dataChegada.getTime()));
			quantidadePessoas.setText(viagem.getQuantidadePessoas().toString());
			orcamento.setText(viagem.getOrcamento().toString());
		}
	}

	public void selecionarData(View view) {
		showDialog(view.getId());
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case R.id.dataSaida:
			return new DatePickerDialog(this, new OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {
					dataSaida.set(year, monthOfYear, dayOfMonth);
					dataSaidaButton.setText(dateFormat.format(dataSaida
							.getTime()));
				}
			}, dataSaida.get(Calendar.YEAR), dataSaida.get(Calendar.MONTH),
					dataSaida.get(Calendar.DAY_OF_MONTH));
		case R.id.dataChegada:
			return new DatePickerDialog(this, new OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {
					dataChegada.set(year, monthOfYear, dayOfMonth);
					dataChegadaButton.setText(dateFormat.format(dataChegada
							.getTime()));
				}
			}, dataChegada.get(Calendar.YEAR), dataChegada.get(Calendar.MONTH),
					dataChegada.get(Calendar.DAY_OF_MONTH));
		}
		return null;
	}

	public void salvarViagem(View view) {
		boolean isValido = true;

		if ("".equals(destino.getText().toString().trim())) {
			destino.setError(getString(R.string.campo_obrigatorio));
			isValido = false;
		}

		if ("".equals(orcamento.getText().toString().trim())) {
			orcamento.setError(getString(R.string.campo_obrigatorio));
			isValido = false;
		}

		if ("".equals(quantidadePessoas.getText().toString().trim())) {
			quantidadePessoas.setError(getString(R.string.campo_obrigatorio));
			isValido = false;
		}

		if (isValido) {
			Viagem viagem = new Viagem();
			viagem.setId(id == null ? -1 : Long.valueOf(id));
			viagem.setDestino(destino.getText().toString().trim());
			viagem.setDataSaida(dataSaida.getTime());
			viagem.setDataChegada(dataChegada.getTime());
			viagem.setOrcamento(Double.valueOf(orcamento.getText().toString()
					.trim()));
			viagem.setQuantidadePessoas(Integer.valueOf(quantidadePessoas
					.getText().toString().trim()));
			viagem.setTipoViagem(radioGroup.getCheckedRadioButtonId() == R.id.lazer ? Constantes.VIAGEM_LAZER
					: Constantes.VIAGEM_NEGOCIOS);

			long resultado;
			if (id == null) {
				resultado = viagemDAO.insert(viagem);
				id = String.valueOf(resultado);
			} else {
				resultado = viagemDAO.update(viagem);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.viagem_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.novo_gasto:
			startActivity(new Intent(this, GastoActivity.class));
			return true;
		case R.id.remover:
			return viagemDAO.delete(id);
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	@Override
	protected void onDestroy() {
		viagemDAO.close();
		super.onDestroy();
	}
}
