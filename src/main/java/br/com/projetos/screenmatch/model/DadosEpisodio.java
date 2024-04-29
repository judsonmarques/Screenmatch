package br.com.projetos.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)// <- Ignorar o dados não listados
public record DadosEpisodio (@JsonAlias("Title") String titulo,
                             @JsonAlias("Episodes") Integer episodio,
                             @JsonAlias("imdRating") String avaliacao,
                             @JsonAlias("Released") String dataLancamento
                                ){
}
