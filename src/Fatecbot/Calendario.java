package Fatecbot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class Calendario {
    private LocalDate Hoje;
    private HashMap<String, Feriado> Feriados = new HashMap();
    private HashMap<String, DiaSemAula> DiasSemAula = new HashMap();
    
    public Calendario(String Arquivo)throws FileNotFoundException, UnsupportedEncodingException{
        Scanner arquivo = new Scanner(new File(Arquivo)).useDelimiter("\n");
        List<String> linhas = new ArrayList();
        while (arquivo.hasNext()) linhas.add(arquivo.next());
        arquivo.close();
        this.LerArquivo(linhas);
        this.Hoje = LocalDate.now();
    }

    private void LerArquivo(List<String> Linhas){
        String secao = null;
        for (String linha : Linhas){
            if (linha.startsWith(";") || linha.startsWith("#")) continue;
            
            if (linha.startsWith("[") && linha.endsWith("]")){
                secao = linha.substring(1, linha.length() - 1);
                continue;
            }
            
            String data = linha.substring(0, linha.indexOf("=")).trim();
            String titulo = linha.substring(linha.indexOf("=") + 1).trim();
            
            if (secao == "Feriado")
                Feriados.put(data, new Feriado(data, titulo));
            else if (secao == "DiasSemAula")
                DiasSemAula.put(data, new DiaSemAula(data, titulo));
            else System.out.println("Linha '" + linha + "' não pertence a uma chave e será ignorado");
        }
    }
    
    private String Hgd(String Dia, String Mes, HashMap Tipo){
        Iterator it = Tipo.entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry pair = (Map.Entry)it.next();
        System.out.println(pair.getKey() + " = " + pair.getValue());
        it.remove();
    }
        
        return null;
    }
    
    public String ProximoFeriado(){
        return null;
    }
    public String ProximoDiaSemAula(){
        return null;
    }
    public String NaDataTeraAula(String Data){
        return null;
    }
}
