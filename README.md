# FitCorrect
[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.15346783.svg)](https://doi.org/10.5281/zenodo.15346783)

O FitCorrect é um protótipo de aplicativo móvel desenvolvido como Trabalho de Conclusão de Curso (TCC) para o curso de Bacharelado em Ciência da Computação. O objetivo do projeto é auxiliar usuários na prática correta de exercícios físicos, utilizando tecnologia de visão computacional para fornecer feedback em tempo real sobre a postura e a execução dos movimentos.

A arquitetura do projeto é dividida em três partes principais:

- Frontend: Um aplicativo Android onde o usuário interage com as funcionalidades do sistema, usando o Jetpack Compose. 
- Backend: Um serviço de backend feito em Kotlin que gerencia o banco de dados, autenticação de usuários e a lógica do negócio.
- Visão Computacional: A biblioteca MediaPipe é o coração da funcionalidade de feedback, responsável por capturar e analisar os movimentos do usuário.
