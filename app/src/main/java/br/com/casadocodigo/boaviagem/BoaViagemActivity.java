package br.com.casadocodigo.boaviagem;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class BoaViagemActivity extends Activity {

	private EditText usuario, senha;
	private CheckBox manterConectado;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		this.usuario = (EditText) findViewById(R.id.usuario);
		this.senha = (EditText) findViewById(R.id.senha);
		this.manterConectado = (CheckBox) findViewById(R.id.manterConectado);

		SharedPreferences preferences = getSharedPreferences(
				Constantes.MANTER_CONECTADO, 0);
		boolean conectado = preferences.getBoolean(Constantes.MANTER_CONECTADO,
				false);

		if (conectado) {
			startActivity(new Intent(this, DashboardActivity.class));
		}
	}

	public void entrarOnClick(View v) {
		String usuarioInformado = this.usuario.getText().toString().trim();
		String senhaInformada = this.senha.getText().toString().trim();

		if (usuarioInformado.equals("leitor") && senhaInformada.equals("123")) {
			SharedPreferences preferencias = getSharedPreferences(
					Constantes.MANTER_CONECTADO, 0);

			Editor editor = preferencias.edit();
			editor.putBoolean(Constantes.MANTER_CONECTADO,
					manterConectado.isChecked());
			editor.commit();

			startActivity(new Intent(this, DashboardActivity.class));
		} else {
			String mensagemErro = getString(R.string.erro_autenticacao);
			Toast toast = Toast
					.makeText(this, mensagemErro, Toast.LENGTH_SHORT);
			toast.show();
		}
	}
}
