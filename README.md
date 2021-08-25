[![Discord](https://img.shields.io/discord/870339990335406090?color=fff&label=Discord&logo=discord&logoColor=fff)](https://discord.gg/wrQUrvJAFS)


Dynasty Discord Bot Dokumentation
=============

Willkommen zur Dokumentation des Discord Bots. Hier findet ihr Anleitungen zur Installation, Konfiguration und Weiterentwicklung. Zusätzlich dazu findet ihr hier eine Dokumentation über die Bestandteile des Projektes, darunter zählen zum Beispiel die Branches- und ihre Aufgaben die (wenn vorhanden) Module und weitere wichtige Hinweise, die noch vor dem eigentlichen Entwickeln wichtig für euch sein können.

---
## Installation:
Die Installation des Discord Bots erfolgt über das Klonen in GitHub. Wir empfehlen dir für deine Projekte einen WorkSpace Ordner zu erstellen, in welchem du dann einen GitHub Ordner hast, in welchem widerrum nach Usern oder in unserem Fall Organisationen spezifiziert wird. In diesem Beispiel sieht das dann so aus:

```
D:\Dokumente\WorkSpace\GitHub\project-dynasty\discord-bot
```

### Das Projekt mit CLI klonen
```ssh
gh repo clone project-dynasty/discord-bot
```

---
## Branches:
* `production`: Produktionsbuild (Wird automatisch auf die entsprechenden Dienste deployed)
* `release/*`: Release Builds (z.B. `release/2021.8.2`, eine stabil laufende Version benannt nach dem Zeitpunkt)
* `bug/*`: Bug fix Branch (z.B. `bug/meta-bug`, für Bug fixes)
* `feature/*`: Feature Update Branch (z.B. `feature/level-update`, für große Updates)
* `dev/*`: Developer spezifischer Branch (z.B. `dev/nicokempe`, für kleine Änderungen und nur temporär ausgelegt)

---
## Lizensierung:
Niemand außer und dem Nico Kempe Einzelunternehmen steht es zu den geschriebenen Code ohne Erlaubnis zu verwenden. Bei eigenem Einsatz in das Projekt gibt man automatisch alle Rechte an der aufgewendeten Arbeit ab.
