package Fatecbot;

public class DiaSemAula {
    
    public String Titulo;
    public String Data;
    
    public DiaSemAula(String Data, String Titulo){
        this.Data = Data;
        this.Titulo = Titulo;
    }
    
    public String Descricao(){
        return this.TituloEPeriodo();
    }
    
    protected String TituloEPeriodo(){
        if (this.Data.contains("-")) return this.Titulo;
        return this.Titulo + " que vai de " + this.Data.substring(0, 5) +
                " até " + this.Data.substring(6);
    }
}
