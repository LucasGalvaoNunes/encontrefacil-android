package opetbrothers.com.encontrefacil.Util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import org.w3c.dom.Text;

import java.text.NumberFormat;

/**
 * Created by Lucas Galvao Nunes on 06/06/2017.
 */

public class PatternsUtil{
    private EditText editText;
    private TextWatcher pPatternTelefone;
    private TextWatcher pPatternCPF;
    private TextWatcher pPatternCNPJ;
    private TextWatcher pPatternPreco;

    public PatternsUtil(EditText editText) {
        this.editText = editText;
        this.pPatternTelefone = new Telefone();
        this.pPatternCPF = new CPF();
        this.pPatternCNPJ = new CNPJ();
        this.pPatternPreco = new Preco();
    }

    public EditText getEditText() {
        return editText;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public TextWatcher getpPatternTelefone() {
        return pPatternTelefone;
    }

    public void setpPatternTelefone(TextWatcher pPatternTelefone) {
        this.pPatternTelefone = pPatternTelefone;
    }

    public TextWatcher getpPatternCPF() {
        return pPatternCPF;
    }

    public void setpPatternCPF(TextWatcher pPatternCPF) {
        this.pPatternCPF = pPatternCPF;
    }

    public TextWatcher getpPatternCNPJ() {
        return pPatternCNPJ;
    }

    public void setpPatternCNPJ(TextWatcher pPatternCNPJ) {
        this.pPatternCNPJ = pPatternCNPJ;
    }


    public TextWatcher getpPatternPreco() {
        return pPatternPreco;
    }

    public void setpPatternPreco(TextWatcher pPatternPreco) {
        this.pPatternPreco = pPatternPreco;
    }

    public class CNPJ implements TextWatcher{
        int len=0;
        @Override
        // ##.###.###/####-##
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            String str = editText.getText().toString();
            len = str.length();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String str = editText.getText().toString();

            if(str.length()== 2 && len < str.length()){//len check for backspace
                editText.append(".");
            }
            if(str.length()== 6 && len < str.length()){//len check for backspace
                editText.append(".");
            }
            if(str.length()== 10 && len < str.length()){//len check for backspace
                editText.append("/");
            }
            if(str.length()== 15 && len < str.length()){//len check for backspace
                editText.append("-");
            }
        }
    }
    public class CPF implements TextWatcher{
        int len=0;
        @Override
        // ###.###.###-##
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            String str = editText.getText().toString();
            len = str.length();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String str = editText.getText().toString();

            if(str.length()== 3 && len < str.length()){//len check for backspace
                editText.append(".");
            }
            if(str.length()== 7 && len < str.length()){//len check for backspace
                editText.append(".");
            }
            if(str.length()== 11 && len < str.length()){//len check for backspace
                editText.append("-");
            }
        }
    }
    public class Telefone implements TextWatcher{

        int len=0;
        @Override
        // ## #####-####
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            String str = editText.getText().toString();
            len = str.length();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String str = editText.getText().toString();

            if(str.length()== 2 && len < str.length()){//len check for backspace
                editText.append(" ");
            }
            if(str.length()== 8 && len < str.length()){//len check for backspace
                editText.append("-");
            }
        }
    }
    public class Preco implements TextWatcher{
        private NumberFormat nf = NumberFormat.getCurrencyInstance();

        private boolean isUpdating = false;
        int len=0;
        @Override
        // ## #####-####
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Evita que o método seja executado varias vezes.
            // Se tirar ele entre em loop
            if (isUpdating) {
                isUpdating = false;
                return;
            }
            isUpdating = true;
            String str = s.toString();
            // Verifica se já existe a máscara no texto.
            boolean hasMask = ((str.indexOf("R$") > -1 || str.indexOf("$") > -1) &&
            (str.indexOf(".") > -1 || str.indexOf(",") > -1));
            // Verificamos se existe máscara
            if (hasMask) {
                // Retiramos a máscara.
                str = str.replaceAll("[R$]", "").replaceAll("[,]", "")
                        .replaceAll("[.]", "");
            }

            try {
                // Transformamos o número que está escrito no EditText em
                // monetário.
                str = nf.format(Double.parseDouble(str) / 100);
                editText.setText(str);
                editText.setSelection(editText.getText().length());
            } catch (NumberFormatException e) {
                s = "";
            }

        }
    }
}
