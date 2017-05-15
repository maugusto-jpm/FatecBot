package Fatecbot;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.DayOfWeek;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class Calendario {
    public static LocalDate Hoje;
    private final HashMap<String, Feriado> Feriados = new HashMap();
    private final HashMap<String, DiaSemAula> DiasSemAula = new HashMap();
    
    public Calendario(String Arquivo) throws FileNotFoundException{
        Scanner arquivo = new Scanner(new File(Arquivo)).useDelimiter("\n|\r\n");
        List<String> linhas = new ArrayList();
        while (arquivo.hasNext()) linhas.add(arquivo.next().replace("﻿", ""));
        arquivo.close();
        this.lerArquivo(linhas);
        Calendario.Hoje = LocalDate.now();
    }

    private void lerArquivo(List<String> Linhas){
        String secao = "";
        for (String linha : Linhas){
            if (linha.isEmpty() || linha.startsWith(";") || linha.startsWith("#")) continue;
            
            if (linha.startsWith("[") && linha.endsWith("]")){
                secao = linha.substring(1, linha.length() - 1);
                continue;
            }
            String data = linha.substring(0, linha.indexOf("=")).trim();
            String titulo = linha.substring(linha.indexOf("=") + 1).trim();
            switch (secao) {
                case "Feriado":
                    Feriados.put(data, new Feriado(data, titulo));
                    break;
                case "DiasSemAula":
                    DiasSemAula.put(data, new DiaSemAula(data, titulo));
                    break;
                default:
                    System.out.println("Linha '" + linha + "' não pertence a uma chave e será ignorada");
                    break;
            }
        }
    }
    
    private String proximaOcorrencia(List<String> Datas){
        HashMap<Integer, List<Integer>> datas = new HashMap();
        
        for (String data : Datas) {
            int dia = Integer.parseInt(data.substring(0, 2)), mes;
            
            if (data.contains("-")) mes = Integer.parseInt(data.substring(3, 5));
            else mes = Integer.parseInt(data.substring(3));
            
            if (datas.containsKey(mes)){
                List<Integer> m = datas.get(mes);
                m.add(dia);
            }
            else{
                List<Integer> m = new ArrayList();
                m.add(dia);
                datas.put(mes, m);
            }
        }        
        int dia = Calendario.Hoje.getDayOfMonth() + 1;
        for (int mes = Calendario.Hoje.getMonth().ordinal() + 1; mes <= 12; mes++){
            if (datas.containsKey(mes))              
                for (Integer d : datas.get(mes))
                    if (d >= dia){
                        String dd = String.valueOf(d);
                        if (dd.length() == 1) dd = "0" + dd;
                        String mm = String.valueOf(mes);
                        if (mm.length() == 1) mm = "0" + mm;
                        dd = dd + "/" + mm;
                        if (!Datas.contains(dd)){
                            for (String dt : Datas)
                                if (dt.startsWith(dd)) return dt;
                        }
                        return dd;
                    }
            dia = 1;
        }
        return null;
    }
    
    public String proximoFeriado(){
        List<String> lista = new ArrayList();
        Iterator it = this.Feriados.entrySet().iterator();
        while (it.hasNext()) lista.add((String)((Map.Entry)it.next()).getKey());
        
        String data = this.proximaOcorrencia(lista);
        if (data != null)
            return "Em " + data + " será " +
                this.Feriados.get(data).Descricao();
        
        return "Nenhum feriado encontrado";
    }
    public String proximoDiaSemAula(){
        List<String> lista = new ArrayList();
        Iterator it = this.Feriados.entrySet().iterator();
        while (it.hasNext()) lista.add((String)((Map.Entry)it.next()).getKey());
        
        it = this.DiasSemAula.entrySet().iterator();
        while (it.hasNext()) lista.add((String)((Map.Entry)it.next()).getKey());
        
        String data = this.proximaOcorrencia(lista);
        if (data != null){
            DiaSemAula dsa;
            if (this.Feriados.containsKey(data)) return this.Feriados.get(data).periodo();
            return this.DiasSemAula.get(data).periodo();
        }
        
        return "Nenhum dia sem aula encontrado";
    }
    private String naDataTeraAula(String Data){
        LocalDate data = LocalDate.parse(Data + "/" + Calendario.Hoje.getYear(),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (data.isBefore(Calendario.Hoje)) data = data.plusYears(1);

        for (Map.Entry item : this.Feriados.entrySet()){
            DiaSemAula d = (Feriado)item.getValue();
            if (d.DataCoincide(Data)){
                return "Não. Em " + Data + " será " + d.Descricao();
            }
        }
        
        for (Map.Entry item : this.DiasSemAula.entrySet()){
            DiaSemAula d = (DiaSemAula)item.getValue();
            if (d.DataCoincide(Data)){
                return "Não. Em " + Data + " será " + d.Descricao();
            }
        }
        
        if (data.getDayOfWeek() == DayOfWeek.SATURDAY)
            return "Não. " + Data + " será um sábado";
        else if (data.getDayOfWeek() == DayOfWeek.SUNDAY)
            return "Não. " + Data + " será um domingo";
        
        return "Sim. Em " + Data + " terá aula";
    }
    public String listarDiasSemAula(){
        String resultado = "";
        for (HashMap.Entry dsa : this.DiasSemAula.entrySet()) {
            if (!resultado.isEmpty()) resultado += "\n";
            resultado += String.valueOf(dsa.getKey()).replace("-", " a ") +
                    " - " + ((DiaSemAula)dsa.getValue()).Titulo;
        }
        if (!resultado.isEmpty()) resultado += "\n";
        return resultado += this.listarFeriados();
    }
    public String listarFeriados(){
        String resultado = "";
        for (HashMap.Entry dsa : this.Feriados.entrySet()) {
            if (!resultado.isEmpty()) resultado += "\n";
            resultado += dsa.getKey() + " - " + ((Feriado)dsa.getValue()).Titulo;
        }
        return resultado;
    }
    public String amanhaTeraAula(){
        LocalDate hoje = LocalDate.now().plusDays(1);
        return this.naDataTeraAula(hoje.getDayOfMonth() + "/" + hoje.getMonthValue());
    }
    public String nesteDiaTeraAula(String dia) throws Exception{
        LocalDate hoje = LocalDate.now();
        DayOfWeek ds;
        if (dia.contains("segunda")) ds = DayOfWeek.MONDAY;
        else if (dia.contains("terça") || dia.contains("terca")) ds = DayOfWeek.TUESDAY;
        else if (dia.contains("quarta")) ds = DayOfWeek.WEDNESDAY;
        else if (dia.contains("quinta")) ds = DayOfWeek.THURSDAY;
        else if (dia.contains("sexta")) ds = DayOfWeek.FRIDAY;
        else if (dia.contains("depois de amanhã") || dia.contains("depois de amanha")) {
            hoje = LocalDate.now().plusDays(2);
            String mes = String.valueOf(hoje.getMonthValue());
            if (mes.length() == 1) mes = "0" + mes.toCharArray()[0];
            return this.naDataTeraAula(hoje.getDayOfMonth() + "/" + mes);
        }
        else if (dia.contains("amanhã") || dia.contains("amanha")) {
            hoje = LocalDate.now().plusDays(1);
            String mes = String.valueOf(hoje.getMonthValue());
            if (mes.length() == 1) mes = "0" + mes.toCharArray()[0];
            return this.naDataTeraAula(hoje.getDayOfMonth() + "/" + mes);
        }
        else {
            int indice = dia.indexOf("/");
            String[] data = dia.substring(indice - 2, indice + 3).split("/");
            data[0] = data[0].replace(" ", "0");
            if (data[1].contains(" ")) data[1] = "0" + data[1].toCharArray()[0];
            return this.naDataTeraAula(data[0] + "/" + data[1]);
        }
        for (int i = 1; i <= 7; i++){
            hoje = hoje.plusDays(1);
            if (hoje.getDayOfWeek() == ds)
                break;
        }
        String mes = String.valueOf(hoje.getMonthValue());
        if (mes.length() == 1) mes = "0" + mes.toCharArray()[0];
        return this.naDataTeraAula(hoje.getDayOfMonth() + "/" + mes);
    }
}