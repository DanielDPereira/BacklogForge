package com.backlogforge.application.ai;

/**
 * Interface abstrata para integração com Provedores de Inteligência Artificial.
 * Permite desacoplar a lógica de geração da implementação específica do modelo/provedor.
 */
public interface AiService {

    /**
     * Envia um prompt de texto simples e retorna a resposta textual do modelo.
     *
     * @param prompt Texto do prompt a ser processado.
     * @return Resposta textual gerada pelo modelo.
     */
    String generate(String prompt);

    /**
     * Envia um prompt estruturado e desserializa a resposta no tipo especificado.
     *
     * @param prompt       Texto do prompt a ser processado.
     * @param responseType Classe de destino para a resposta estruturada.
     * @param <T>          Tipo da resposta estruturada.
     * @return Objeto populado com a resposta da IA.
     */
    <T> T generateStructured(String prompt, Class<T> responseType);
}
