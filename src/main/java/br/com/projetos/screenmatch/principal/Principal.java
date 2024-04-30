package br.com.projetos.screenmatch.principal;

import br.com.projetos.screenmatch.model.DadosEpisodio;
import br.com.projetos.screenmatch.model.DadosSeries;
import br.com.projetos.screenmatch.model.DadosTemporada;
import br.com.projetos.screenmatch.model.Episodios;
import br.com.projetos.screenmatch.service.ConsumoAPI;
import br.com.projetos.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();

   private static final String ENDERECO = "https://www.omdbapi.com/?t=";
   private static final String API_KEY = "&apikey=6585022c";

   public void exibeMenu(){
       System.out.println("Digite o nome da série ");
       var nomeSerie = leitura.nextLine();
       var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
       DadosSeries dados = conversor.obterDados(json, DadosSeries.class);
       System.out.println(dados);

       List<DadosTemporada> temporadas = new ArrayList<>();

       for (int i = 1; i<=dados.totalTemporadas(); i++){
           json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
           DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
           temporadas.add(dadosTemporada);
       }
       temporadas.forEach(System.out::println);

       temporadas.forEach(t ->t.episodios().forEach(e -> System.out.println(e.titulo())));



       List<DadosEpisodio> dadosEpisodios = temporadas.stream()
               .flatMap(t -> t.episodios().stream())
               .collect(Collectors.toList());

       System.out.println("\n Top 5 Episodios" );
       dadosEpisodios.stream()
               .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
               .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
               .limit(5)
               .forEach(System.out::println);


       List<Episodios> episodios = temporadas.stream()
               .flatMap(t -> t.episodios().stream()
                       .map(d -> new Episodios(t.numeros(), d))
               ).collect(Collectors.toList());

       episodios.forEach(System.out::println);

       System.out.println("A partir de que ano você deseja ver os episódios? ");
       var ano = leitura.nextInt();
       leitura.nextLine();

       LocalDate dataBusca = LocalDate.of(ano, 1, 1);

       DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

       episodios.stream()
               .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
               .forEach(e -> System.out.println(
                       "Temporada:  " + e.getTemporada() +
                               " Episódio: " + e.getTitulo() +
                               " Data lançamento: " + e.getDataLancamento().format(formatador)
               ));



   }

}