package Fatecbot;

import java.util.List;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;
// Comentário de teste

public class Principal {
    private static Calendario Calendario;
    public static void main(String[] args) {

        try{
            Calendario = new Calendario("Calendario.ini");
        } catch (Exception ex){
            System.out.println("Não foi possível ler o arquivo 'Calendario.ini'");
        }
        
        //Criação do objeto bot com as informações de acesso
        TelegramBot bot = TelegramBotAdapter.build("361656060:AAEPisg7kMRaT849cMCktr_gcYGKtcK6M7U");
        //objeto responsável por receber as mensagens
        GetUpdatesResponse Pergunta;
        //objeto responsável por gerenciar o envio de respostas
        SendResponse Resposta;
        //objeto responsável por gerenciar o envio de ações do chat
        BaseResponse Acoes;

        //controle de off-set, isto é, a partir deste ID será lido as mensagens pendentes na fila
        int m=0;

        //loop infinito pode ser alterado por algum timer de intervalo curto
        while (true){
            //executa comando no Telegram para obter as mensagens pendentes a partir
            //de um off-set (limite inicial)
            Pergunta =  bot.execute(new GetUpdates().limit(100).offset(m));

            //lista de mensagens
            List<Update> updates = Pergunta.updates();

            //análise de cada ação da mensagem
            for (Update update : updates) {
                //atualização do off-set
                m = update.updateId()+1;

                System.out.println("Recebendo: " + update.message().text());

                //envio de "Escrevendo" antes de enviar a resposta
                Acoes = bot.execute(new SendChatAction(update.message().chat().id(),
                        ChatAction.typing.name()));
                
                //verificação de ação de chat foi enviada com sucesso
                System.out.println("Resposta de Chat Action Enviada?: " + Acoes.isOk());
                String resposta = gerarResposta(update.message().text());

                System.out.println("Enviando: " + resposta);
                //envio da mensagem de resposta
                Resposta = bot.execute(new SendMessage(update.message().chat().id(), resposta));
                
                //verificação de mensagem enviada com sucesso
                System.out.println("Mensagem Enviada?: " + Resposta.isOk());
            }
        }
    }
    private static String gerarResposta(String Mensagem){
        Mensagem = Mensagem.toLowerCase();
        
        if (Mensagem.equals("dicas"))
            return "Exemplos de perguntas e comandos disponíveis:\n" +
                    "Qual o próximo dia sem aula?\n" +
                    "Em 14/05 vai ter aula?\n" +
                    "Lista de dias sem aula";
        
        if ((Mensagem.startsWith("em ") || Mensagem.startsWith("dia ") || Mensagem.startsWith("na data "))
                && (Mensagem.endsWith("vai ter aula?") || Mensagem.endsWith("haverá aula?") ||
                Mensagem.endsWith("havera aula?") || Mensagem.endsWith("terá aula?") || Mensagem.endsWith("tera aula?")))
        {
            int indice = Mensagem.indexOf("/");
            return Calendario.naDataTeraAula(Mensagem.substring(indice - 2, indice + 3).replace(' ', '0'));
        }
        
        if (Mensagem.endsWith("próximo dia sem aula?") || Mensagem.endsWith("proximo dia sem aula?"))
            return Calendario.proximoDiaSemAula();
        
        if (Mensagem.contains("lista")) return Calendario.listarDiasSemAula();
        
        return "Não entendi...\n" +
                "Lembre-se de encerrar perguntas com ponto de interrogação.\n\n"+
                "Você pode digitar 'dicas' para ver as perguntas disponíveis";
    }
}
