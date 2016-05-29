:- style_check(-singleton).
:- ensure_loaded(library(lists)).

:-dynamic(proximo_passo/5).

:-dynamic(posicao/2).

posicao(0,11).

/* Na própria casa */
:-dynamic(brilho_diamante/2).
:-dynamic(brilho_energia/2).

/* Nas casas adjacentes */
:-dynamic(barulho/2).
:-dynamic(brisa/2).
:-dynamic(flash/2).

:-dynamic(energia/1).

energia(100).

:-dynamic(visitada/2).

visitada(0,11).

:-dynamic(segura/2).

:- dynamic(custo/1).

custo(0).

/* Usados para definir no mapa as casas que não estão vazias */
:-dynamic(inimigo/3).	% X,Y,Poder
:-dynamic(buraco/2).
:-dynamic(moeda_energia/2).
:-dynamic(vortice/2).
:-dynamic(diamante/2).

/* Número de casa visitadas */
:-dynamic(num_movimentos/1).

num_movimentos(0).

/* Quantidade de ataques do agente restantes */
:-dynamic(num_ataques/1).

num_ataques(5).

/* Todas as casas do mapa */
:-dynamic(valida/2).

:-dynamic(direcao/1).

direcao(norte).

/* Se detectar alguma coisa declara o fato para as adjacentes */
:-dynamic(possivel_vortice/2).
:-dynamic(possivel_inimigo/2).
:-dynamic(possivel_buraco/2).

/* Atualizado após tentar inserir um dos fatos acima pela segunda vez */
:-dynamic(tem_inimigo/3).
:-dynamic(tem_buraco/2).
:-dynamic(tem_vortice/2).

/* Para marcar a casa que tem energia */
:-dynamic(tem_energia/2).

/* Para marcar casa com diamante */
:-dynamic(tem_diamante/2).

/* Quantidade de diamantes coletados */
:-dynamic(num_diamantes/1).

num_diamantes(0).

/* Casa segura; não sentiu brisa,barulho ou flash de uma adjacente */
:-dynamic(verificada/2).

/* Usado para tentar evitar loops */
:- dynamic(anterior/2).
													
perigosa(X,Y) :- possivel_inimigo(X,Y) ; possivel_buraco(X,Y) ; possivel_vortice(X,Y).
ruim(X,Y) :- tem_vortice(X,Y) ; tem_buraco(X,Y) ; tem_inimigo(X,Y,_).
	
segura(X,Y) :- not(perigosa(X,Y)), not(visitada(X,Y)) , not(ruim(X,Y)).
segura(X,Y) :- visitada(X,Y), not(tem_inimigo(X,Y,_)).

/* Tenta ir para adjacente na mesma direção que seja segura e não tenha sido visitada */
adjacente_segura(X,Y,XR,YR) :- XD is X+1,valida(XD,Y),(segura(XD,Y)),not(visitada(XD,Y)),direcao(leste),XR is XD, YR is Y.
adjacente_segura(X,Y,XR,YR) :- YC is Y-1,valida(X,YC),(segura(X,YC)),not(visitada(X,YC)),direcao(norte),XR is X, YR is YC.
adjacente_segura(X,Y,XR,YR) :- XE is X-1,valida(XE,Y),(segura(XE,Y)),not(visitada(XE,Y)),direcao(oeste),XR is XE, YR is Y.
adjacente_segura(X,Y,XR,YR) :- YB is Y+1,valida(X,YB),(segura(X,YB)),not(visitada(X,YB)),direcao(sul),XR is X, YR is YB.
/* Tenta ir para adjacente em outra direção que seja segura e não tenha sido visitada */
adjacente_segura(X,Y,XR,YR) :- XD is X+1,valida(XD,Y),(segura(XD,Y)),not(visitada(XD,Y)),XR is XD, YR is Y.
adjacente_segura(X,Y,XR,YR) :- YC is Y-1,valida(X,YC),(segura(X,YC)),not(visitada(X,YC)),XR is X, YR is YC.
adjacente_segura(X,Y,XR,YR) :- XE is X-1,valida(XE,Y),(segura(XE,Y)),not(visitada(XE,Y)),XR is XE, YR is Y.
adjacente_segura(X,Y,XR,YR) :- YB is Y+1,valida(X,YB),(segura(X,YB)),not(visitada(X,YB)),XR is X, YR is YB.

/* Procura a última casa marcada como verificada */
procura_verificada(XR,YR) :- verificada(_,_),findall([X,Y],verificada(X,Y),L),last(L,E),nth1(1,E,XS),nth1(2,E,YS),XR is XS,YR is YS,!.

/* Vai para adjacente na mesma direção que seja segura que foi visitada */
segura_visitada(X,Y,XR,YR) :- XD is X+1,valida(XD,Y),segura(XD,Y),direcao(leste),XR is XD, YR is Y.
segura_visitada(X,Y,XR,YR) :- YC is Y-1,valida(X,YC),segura(X,YC),direcao(norte),XR is X, YR is YC.
segura_visitada(X,Y,XR,YR) :- XE is X-1,valida(XE,Y),segura(XE,Y),direcao(oeste),XR is XE, YR is Y.
segura_visitada(X,Y,XR,YR) :- YB is Y+1,valida(X,YB),segura(X,YB),direcao(sul),XR is X, YR is YB.
/* Se não conseguir ir para casa segura na mesma direção,vá para uma casa segura em outra direção que não seja a anterior */
segura_visitada(X,Y,XR,YR) :- YB is Y+1,valida(X,YB),segura(X,YB),not(anterior(X,YB)),XR is X, YR is YB.
segura_visitada(X,Y,XR,YR) :- XD is X+1,valida(XD,Y),segura(XD,Y),not(anterior(XD,Y)),XR is XD, YR is Y.
segura_visitada(X,Y,XR,YR) :- YC is Y-1,valida(X,YC),segura(X,YC),not(anterior(X,YC)),XR is X, YR is YC.
segura_visitada(X,Y,XR,YR) :- XE is X-1,valida(XE,Y),segura(XE,Y),not(anterior(XE,Y)),XR is XE, YR is Y.
/* Se não conseguir ir para casa segura na mesma direção,vá para uma casa segura em outra direção */
segura_visitada(X,Y,XR,YR) :- YB is Y+1,valida(X,YB),segura(X,YB),XR is X, YR is YB.
segura_visitada(X,Y,XR,YR) :- XD is X+1,valida(XD,Y),segura(XD,Y),XR is XD, YR is Y.
segura_visitada(X,Y,XR,YR) :- YC is Y-1,valida(X,YC),segura(X,YC),XR is X, YR is YC.
segura_visitada(X,Y,XR,YR) :- XE is X-1,valida(XE,Y),segura(XE,Y),XR is XE, YR is Y.


adjacente_vortice(X,Y,XR,YR) :- (XD is X+1,valida(XD,Y),tem_vortice(XD,Y)) -> (XR is XD, YR is Y); (XE is X-1,valida(XE,Y),tem_vortice(XE,Y)) -> (XR is XE, YR is Y); (YC is Y-1,valida(X,YC),tem_vortice(X,YC)) -> (XR is X, YR is YC); (YB is Y+1,valida(X,YB),tem_vortice(X,YB)) -> (XR is X, YR is YB); (XR is X,YR is Y),!.

adjacente_ataque(X,Y,XR,YR) :- (XD is X+1,valida(XD,Y),tem_inimigo(XD,Y,_)) -> (XR is XD, YR is Y); (XE is X-1,valida(XE,Y),tem_inimigo(XE,Y,_)) -> (XR is XE, YR is Y); (YC is Y-1,valida(X,YC),tem_inimigo(X,YC,_)) -> (XR is X, YR is YC); (YB is Y+1,valida(X,YB),tem_inimigo(X,YB,_)) -> (XR is X, YR is YB); (XR is X,YR is Y),!.

/* Auxiliares para verificação da necessidade de atualizações */
verifica_morte(XR,YR,EE,Etot) :- Etot > 0 -> (retract(tem_inimigo(XR,YR,EE)), assert(tem_inimigo(XR,YR,Etot))); (retract(tem_inimigo(XR,YR,EE))), !.
diminui_ataques(X) :- num_ataques(NA),Q is NA-1,retract(num_ataques(NA)), assert(num_ataques(Q)), !.

atualiza_anterior(X,Y) :- retract(anterior(_,_)),assert(anterior(X,Y)).
atualiza_anterior(X,Y) :- assert(anterior(X,Y)).

marcar_energia(X,Y) :- brilho_energia(X,Y),not(tem_energia(X,Y)),assert(tem_energia(X,Y)).
marcar_energia(X,Y) :- true.

confirmar_adjacentes(X,Y) :- XD is X+1,visitada(XD,Y),not(ruim(XD,Y)).
confirmar_adjacentes(X,Y) :- XE is X-1,visitada(XE,Y),not(ruim(XE,Y)).
confirmar_adjacentes(X,Y) :- YC is Y-1,visitada(X,YC),not(ruim(X,YC)).
confirmar_adjacentes(X,Y) :- YB is Y+1,visitada(X,YB),not(ruim(X,YB)).

custo_rotacao(Dir) :- (Dir == oeste;Dir == leste),(direcao(norte);direcao(sul)),retract(custo(B)),O is B - 1,assert(custo(O)),retract(direcao(_)),assert(direcao(Dir)), !.
custo_rotacao(Dir) :- (Dir == norte;Dir == sul),(direcao(leste);direcao(oeste)),retract(custo(B)),O is B - 1,assert(custo(O)),retract(direcao(_)),assert(direcao(Dir)), !.
custo_rotacao(Dir) :- Dir == norte,direcao(sul),retract(custo(B)),O is B - 2,assert(custo(O)),retract(direcao(_)),assert(direcao(Dir)), !.
custo_rotacao(Dir) :- Dir == sul,direcao(norte),retract(custo(B)),O is B - 2,assert(custo(O)),retract(direcao(_)),assert(direcao(Dir)), !.
custo_rotacao(Dir) :- Dir == leste,direcao(oeste),retract(custo(B)),O is B - 2,assert(custo(O)),retract(direcao(_)),assert(direcao(Dir)), !.
custo_rotacao(Dir) :- Dir == oeste,direcao(leste),retract(custo(B)),O is B - 2,assert(custo(O)),retract(direcao(_)),assert(direcao(Dir)), !. 
custo_rotacao(Dir) :- Dir == norte,direcao(norte).
custo_rotacao(Dir) :- Dir == sul,direcao(sul).
custo_rotacao(Dir) :- Dir == leste,direcao(leste).
custo_rotacao(Dir) :- Dir == oeste,direcao(oeste).

verifica_direcao(X,Y,XR,YR,CG) :- SX is (XR-X),SY is (YR - Y),SX < 0,custo_rotacao(oeste).
verifica_direcao(X,Y,XR,YR,CG) :- SX is (XR-X),SY is (YR - Y),SX > 0,custo_rotacao(leste).
verifica_direcao(X,Y,XR,YR,CG) :- SX is (XR-X),SY is (YR - Y),SY < 0,custo_rotacao(norte).
verifica_direcao(X,Y,XR,YR,CG) :- SX is (XR-X),SY is (YR - Y),SY > 0,custo_rotacao(sul).
verifica_direcao(X,Y,XR,YR,CG) :- SX is (XR-X),SY is (YR - Y),SX == 0,SY == 0,GC is 0.

atualiza_custo(X,Y,XR,YR,Val) :- verifica_direcao(X,Y,XR,YR,_),custo(C),NC is C + Val,retract(custo(C)),assert(custo(NC)), !.
verifica_diamante(XR,YR) :- brilho_diamante(XR,YR), assert(tem_diamante(XR,YR)), !.
verifica_diamante(XR,YR) :- true.

marcar_visitada(X,Y) :- not(visitada(X,Y)),verificada(X,Y),assert(visitada(X,Y)),retractall(verificada(X,Y)).
marcar_visitada(X,Y) :- not(visitada(X,Y)),assert(visitada(X,Y)).
marcar_visitada(X,Y) :- visitada(X,Y).

diamante_livre(X,Y,XR,YR) :- (XD is X+1,tem_diamante(XD,Y),not(tem_inimigo(XD,Y,_))) -> (XR is XD, YR is Y); (XE is X-1,tem_diamante(XE,Y),not(tem_inimigo(XE,Y,_))) -> (XR is XE, YR is Y); (YC is Y-1,tem_diamante(X,YC),not(tem_inimigo(X,YC,_))) -> (XR is X, YR is YC); (YB is Y+1,tem_diamante(X,YB),not(tem_inimigo(X,YB,_))) -> (XR is X, YR is YB); (XR is X,YR is Y),!.

/* Distância Manhattan */
distancia(X,Y,XR,YR,D) :- D is abs(X-XR) + abs(Y-YR).

/* Atualizando situação das casas baseadas nos sentidos */

marca_possivel_buraco(X,Y,Xa,Ya) :- not(possivel_buraco(Xa,Ya)),not(visitada(Xa,Ya)),valida(Xa,Ya),not(verificada(Xa,Ya)),assert(possivel_buraco(Xa,Ya)).
marca_possivel_buraco(X,Y,Xa,Ya) :- possivel_buraco(Xa,Ya),not(visitada(Xa,Ya)),valida(Xa,Ya),assert(tem_buraco(Xa,Ya)),retract(possivel_buraco(Xa,Ya)),percepcao_diagonal_buracos(Xa,Ya).
marca_possivel_buraco(X,Y,Xa,Ya) :- true.

marca_possivel_vortice(X,Y,Xa,Ya) :- not(possivel_vortice(Xa,Ya)),not(visitada(Xa,Ya)),valida(Xa,Ya),not(verificada(Xa,Ya)),assert(possivel_vortice(Xa,Ya)).
marca_possivel_vortice(X,Y,Xa,Ya) :- possivel_vortice(Xa,Ya),not(visitada(Xa,Ya)),valida(Xa,Ya),assert(tem_vortice(Xa,Ya)),retract(possivel_vortice(Xa,Ya)),percepcao_diagonal_vortices(Xa,Ya).
marca_possivel_vortice(X,Y,Xa,Ya) :- true.

marca_possivel_inimigo(X,Y,Xa,Ya) :- not(possivel_inimigo(Xa,Ya)),not(visitada(Xa,Ya)),valida(Xa,Ya),not(verificada(Xa,Ya)),assert(possivel_inimigo(Xa,Ya)).
marca_possivel_inimigo(X,Y,Xa,Ya) :- possivel_inimigo(Xa,Ya),not(visitada(Xa,Ya)),valida(Xa,Ya),assert(tem_inimigo(Xa,Ya,100)),retract(possivel_inimigo(Xa,Ya)),percepcao_diagonal_inimigos(Xa,Ya).
marca_possivel_inimigo(X,Y,Xa,Ya) :- true.

marcar_verificada(X,Y) :- not(visitada(X,Y)),valida(X,Y),not(ruim(X,Y)),not(perigosa(X,Y)),not(verificada(X,Y)),assert(verificada(X,Y)).
marcar_verificada(X,Y) :- true.

marcar_verificadas(X,Y) :- Xe is X - 1,Xd is X + 1,Yu is Y - 1,Yd is Y + 1,marcar_verificada(Xe,Y),marcar_verificada(Xd,Y),marcar_verificada(X,Yu), marcar_verificada(X,Yd).

remove_buraco(X,Y) :- tem_buraco(X,Y),retract(tem_buraco(X,Y)),marcar_verificada(X,Y).
remove_buraco(X,Y) :- possivel_buraco(X,Y),retract(possivel_buraco(X,Y)),marcar_verificada(X,Y).
remove_buraco(X,Y) :- true.

remove_vortice(X,Y) :- tem_vortice(X,Y),retract(tem_vortice(X,Y)),marcar_verificada(X,Y).
remove_vortice(X,Y) :- possivel_vortice(X,Y),retract(possivel_vortice(X,Y)),marcar_verificada(X,Y).
remove_vortice(X,Y) :- true.

remove_inimigo(X,Y) :- tem_inimigo(X,Y,_),retract(tem_inimigo(X,Y,_)),marcar_verificada(X,Y).
remove_inimigo(X,Y) :- possivel_inimigo(X,Y),retract(possivel_inimigo(X,Y)),marcar_verificada(X,Y).
remove_inimigo(X,Y) :- true.

percepcao_errada_buracos(X,Y) :- Xe is X - 1,Xd is X + 1,Yu is Y - 1,Yd is Y + 1,remove_buraco(Xe,Y),remove_buraco(Xd,Y),remove_buraco(X,Yd), remove_buraco(X,Yu),!.

percepcao_errada_vortices(X,Y) :- Xe is X - 1,Xd is X + 1,Yu is Y - 1,Yd is Y + 1,remove_vortice(Xe,Y),remove_vortice(Xd,Y),remove_vortice(X,Yu), remove_vortice(X,Yd),!.

percepcao_errada_inimigos(X,Y) :- Xe is X - 1,Xd is X + 1,Yu is Y - 1,Yd is Y + 1,remove_inimigo(Xe,Y),remove_inimigo(Xd,Y),remove_inimigo(X,Yd), remove_inimigo(X,Yd),!.

remove_diagonal_buraco(X,Y) :- (X == 0; X == 11; Y == 0; Y == 11), retract(possivel_buraco(X,Y)),not(possivel_vortice(X,Y)),not(possivel_inimigo(X,Y)),not(ruim(X,Y)),!.
remove_diagonal_buraco(X,Y) :- (X == 0; X == 11; Y == 0; Y == 11), retract(possivel_buraco(X,Y)),!.
remove_diagonal_buraco(X,Y) :- true.

remove_diagonal_vortice(X,Y) :- (X == 0; X == 11; Y == 0; Y == 11), retract(possivel_vortice(X,Y)),not(possivel_buraco(X,Y)),not(possivel_inimigo(X,Y)),not(ruim(X,Y)),!.
remove_diagonal_vortice(X,Y) :- (X == 0; X == 11; Y == 0; Y == 11), retract(possivel_vortice(X,Y)),!.
remove_diagonal_vortice(X,Y) :- true.

remove_diagonal_inimigo(X,Y) :- (X == 0; X == 11; Y == 0; Y == 11), retract(possivel_inimigo(X,Y)),not(possivel_vortice(X,Y)),not(possivel_buraco(X,Y)),!.
remove_diagonal_inimigo(X,Y) :- (X == 0; X == 11; Y == 0; Y == 11), retract(possivel_inimigo(X,Y)),!.
remove_diagonal_inimigo(X,Y) :- true.

percepcao_diagonal_buracos(X,Y) :-  Xe is X - 1,Xd is X + 1,Yu is Y - 1,Yd is Y + 1,remove_diagonal_buraco(Xe,Yu),remove_diagonal_buraco(Xd,Yu), remove_diagonal_buraco(Xe,Yd), remove_diagonal_buraco(Xd,Yd),!.

percepcao_diagonal_vortices(X,Y) :-  Xe is X - 1,Xd is X + 1,Yu is Y - 1,Yd is Y + 1,remove_diagonal_vortice(Xe,Yu),remove_diagonal_vortice(Xd,Yu), remove_diagonal_vortice(Xe,Yd), remove_diagonal_vortice(Xd,Yd),!.

percepcao_diagonal_inimigos(X,Y) :-  Xe is X - 1,Xd is X + 1,Yu is Y - 1,Yd is Y + 1,remove_diagonal_inimigo(Xe,Yu),remove_diagonal_inimigo(Xd,Yu), remove_diagonal_inimigo(Xe,Yd), remove_diagonal_inimigo(Xd,Yd),!.


sentido_brisa(X,Y) :- not(visitada(X,Y)),brisa(X,Y),Xe is X - 1,Xd is X + 1,Yu is Y - 1,Yd is Y + 1,marca_possivel_buraco(X,Y,Xe,Y), marca_possivel_buraco(X,Y,Xd,Y),marca_possivel_buraco(X,Y,X,Yu),marca_possivel_buraco(X,Y,X,Yd),!.
sentido_brisa(X,Y) :- not(brisa(X,Y)),percepcao_errada_buracos(X,Y).
sentido_brisa(X,Y) :- true.

sentido_flash(X,Y) :- not(visitada(X,Y)),flash(X,Y),Xe is X - 1,Xd is X + 1,Yu is Y - 1,Yd is Y + 1,marca_possivel_vortice(X,Y,Xe,Y), marca_possivel_vortice(X,Y,Xd,Y),marca_possivel_vortice(X,Y,X,Yu),marca_possivel_vortice(X,Y,X,Yd),!.
sentido_flash(X,Y) :- not(flash(X,Y)),percepcao_errada_vortices(X,Y).
sentido_flash(X,Y) :- true.

sentido_barulho(X,Y) :- not(visitada(X,Y)),barulho(X,Y),Xe is X - 1,Xd is X + 1,Yu is Y - 1,Yd is Y + 1,marca_possivel_inimigo(X,Y,Xe,Y), marca_possivel_inimigo(X,Y,Xd,Y),marca_possivel_inimigo(X,Y,X,Yu),marca_possivel_inimigo(X,Y,X,Yd),!.
sentido_barulho(X,Y) :- not(barulho(X,Y)),percepcao_errada_inimigos(X,Y).
sentido_barulho(X,Y) :- true.

verifica_sentidos(X,Y) :- not(brisa(X,Y)),not(flash(X,Y)),not(barulho(X,Y)), marcar_verificadas(X,Y),sentido_brisa(X,Y),sentido_flash(X,Y),sentido_barulho(X,Y).
verifica_sentidos(X,Y) :- sentido_brisa(X,Y),sentido_flash(X,Y),sentido_barulho(X,Y).
/* ---------------------------------------------------------------------------------------------------------------------------------- */

/* Ações chamadas após consultar proximo_passo() */
pegar_diamante(X,Y) :- tem_diamante(X,Y),brilho_diamante(X,Y),retract(tem_diamante(X,Y)),retract(brilho_diamante(X,Y)),retract(num_diamantes(N)),Q is N+1,assert(num_diamantes(Q)), atualiza_custo(X,Y,X,Y,1000), !.

pegar_diamante(X,Y) :- brilho_diamante(X,Y),retract(brilho_diamante(X,Y)),retract(num_diamantes(N)),Q is N+1,assert(num_diamantes(Q)),atualiza_custo(X,Y,X,Y,1000), !.

pegar_energia(X,Y) :- retract(brilho_energia(X,Y)),retract(tem_energia(X,Y)),retract(energia(E)),Q is E+20,assert(energia(Q)),atualiza_custo(X,Y,X,Y,-1), !.

pegar_energia(X,Y) :- retract(brilho_energia(X,Y)),retract(energia(E)),Q is E+20,assert(energia(Q)),atualiza_custo(X,Y,X,Y,-1), !.

ir_para_saida(X,Y,XM,YM) :- (retract(posicao(X,Y)),assert(posicao(XM,YM)))->(atualiza_custo(X,Y,XM,YM,-1),marcar_visitada(XM,YM),atualiza_anterior(X,Y)); fail,!.

atacar_diamante(X,Y,XR,YR) :- tem_inimigo(XR,YR,EE),Dano is random(30)+20,Etot is EE - Dano,verifica_morte(XR,YR,EE,Etot),diminui_ataques(_), atualiza_custo(X,Y,XR,YR,-10), !. 

fugir(X,Y,XR,YR) :- retract(posicao(X,Y)), assert(posicao(XR,YR)),atualiza_custo(X,Y,XR,YR,-1),atualiza_anterior(X,Y), !.

andar_seguro(X,Y,XR,YR) :- retract(posicao(X,Y)),assert(posicao(XR,YR)),verifica_sentidos(XR,YR),marcar_visitada(XR,YR),marcar_energia(XR,YR),atualiza_anterior(X,Y), atualiza_custo(X,Y,XR,YR,-1),!.

andar_verificada(X,Y,XR,YR) :- retract(posicao(X,Y)),assert(posicao(XR,YR)),verifica_sentidos(XR,YR),marcar_visitada(XR,YR),marcar_energia(XR,YR),atualiza_anterior(X,Y), atualiza_custo(X,Y,XR,YR,-1),!.

andar(X,Y,XR,YR) :- retract(posicao(X,Y)),assert(posicao(XR,YR)),verifica_sentidos(XR,YR),marcar_visitada(XR,YR),marcar_energia(XR,YR),atualiza_anterior(X,Y), atualiza_custo(X,Y,XR,YR,-1),!.

entrar_casa_inimigo(X,Y,XR,YR) :- not(inimigo(XR,YR,PWR)),retract(tem_inimigo(XR,YR,_)),atualiza_custo(X,Y,XR,YR,-1),retract(posicao(X,Y)),assert(posicao(XR,YR)),verifica_diamante(XR,YR),verifica_sentidos(XR,YR),marcar_visitada(XR,YR),atualiza_anterior(X,Y),!.

entrar_casa_inimigo(X,Y,XR,YR) :- inimigo(XR,YR,PWR)->(retract(energia(E)),NE is E - PWR,assert(energia(NE)),atualiza_custo(X,Y,XR,YR,-1),retract(posicao(X,Y)),assert(posicao(XR,YR)),verifica_diamante(XR,YR),verifica_sentidos(XR,YR),marcar_visitada(XR,YR),atualiza_anterior(X,Y)); fail,!.

entrar_casa_diamante(X,Y,XR,YR) :- (retract(posicao(X,Y)),assert(posicao(XR,YR)))->(atualiza_custo(X,Y,XR,YR,-1),atualiza_anterior(X,Y)); fail,!.

/* Ações em ordem de prioridade */
proximo_passo(fimDeJogo,_,_,_,_) :- energia(E),E < 1.

proximo_passo(fugir,X,Y,XR,YR) :- posicao(X,Y), tem_inimigo(X,Y,_),anterior(XR,YR), !.

proximo_passo(pegar_diamante,X,Y,_,_) :- posicao(X,Y),brilho_diamante(X,Y),!.

proximo_passo(pegar_energia,X,Y,_,_) :- posicao(X,Y), brilho_energia(X,Y), energia(E), E < 90.

proximo_passo(sair,_,_,_,_) :- N is 3, num_diamantes(N).

%proximo_passo(ir_para_saida,X,Y,XR,YR) :- N is 3,num_diamantes(N),posicao(X,Y),XR is 0,YR is 11, !.

proximo_passo(atacar_diamante,X,Y,XR,YR) :- posicao(X,Y),adjacente_ataque(X,Y,XR,YR),not(posicao(XR,YR)),tem_diamante(XR,YR),num_ataques(NA),NA > 0, !.

proximo_passo(entrar_casa_diamante,X,Y,XR,YR) :- posicao(X,Y),diamante_livre(X,Y,XR,YR),not(posicao(XR,YR)),!.

proximo_passo(entrar_casa_inimigo,X,Y,XR,YR) :- posicao(X,Y), adjacente_ataque(X,Y,XR,YR),not(posicao(XR,YR)),energia(E), E > 50, not(visitada(XR,YR)), !.

proximo_passo(procurar_energia,X,Y,XR,YR) :- posicao(X,Y), energia(E), E < 60,tem_energia(XR,YR), !.

proximo_passo(andar_seguro,X,Y,XR,YR) :- posicao(X,Y), adjacente_segura(X,Y,XR,YR), not(posicao(XR,YR)), !.

proximo_passo(andar_verificada,X,Y,XR,YR) :- posicao(X,Y), procura_verificada(XR,YR),not(posicao(XR,YR)),confirmar_adjacentes(XR,YR),distancia(X,Y,XR,YR,D),D > 5, !.

proximo_passo(andar,X,Y,XR,YR) :- posicao(X,Y), segura_visitada(X,Y,XR,YR), not(posicao(XR,YR)), !.

proximo_passo(morrer,_,_,_,_).


