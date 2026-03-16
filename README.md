# Stacking Cups Simulator
**DOPO-POOB 2026-1 — Escuela Colombiana de Ingeniería Julio Garavito**  
**Autor:** Sergio Luis Gonzalez Alba

---

## Descripcion
Simulador del problema Stacking Cups (ICPC 2025, Problema J). Consiste en apilar tazas cilindricas en una torre y resuelve el problema de encontrar el orden que logre una altura objetivo exacta.

## Clases principales
| Clase | Descripción |
|-------|-------------|
| `Tower` | Simulador visual principal |
| `Cup` / `Lid` | Taza y tapa con representación visual |
| `StackingCups` | Resuelve el algoritmo de la maratón |
| `TowerC2Test` | 20 pruebas JUnit ciclo 2 |
| `TowerContestTest` | 23 pruebas JUnit ciclo 3 |

## Uso rápido
```java
// Simular la solución del problema
StackingCups sc = new StackingCups(4, 9);
sc.solve();
Tower t = new Tower(10, 20);
t.simulate(sc.getSolution());
```

## Requisitos
- BlueJ 5.x — Java 8 o superior
