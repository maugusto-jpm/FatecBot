package Fatecbot;
public class Feriado extends DiaSemAula{
    
    public Feriado(String Data, String Titulo){
        super(Data, Titulo);
    }
    
    @Override
    public String Descricao(){
        return "feriado de " + this.Titulo;
    }
    
    @Override
    public Boolean DataCoincide(String Data){
        return (Data == null ? super.Data == null : Data.equals(super.Data));
    }
    
    @Override
    public String periodo(){
        if (!this.Data.contains("-")) return "Em " + this.Data + " será feriado de " + this.Titulo;
        return "De " + this.Data.substring(0, 5) + " a " + this.Data.substring(6) + " será feriado de " + this.Titulo;
    }
}
