
# Sample Recommender

Este proyecto provee una aplicación Streamlit que ingresa metadatos de NSynth (base de datos) en Neo4j Aura y ofrece dos modos de interacción:

- **Expertos**: filtran muestras usando todos los atributos disponibles.  
- **Inexpertos**: filtran sobre la selección previa de expertos y obtienen métricas de acierto.

---

## Requisitos

1. **Python 3.8+**  
2. **Neo4j Aura**: una instancia en `neo4j+s://...neo4j.io:7687` con usuario (“neo4j”) y contraseña configurados.  
3. **Dependencias Python** (listadas en `requirements.txt`):  
   - `streamlit`  
   - `pandas`  
   - `datasets`  
   - `neo4j`  
   - `librosa`  
   - `soundfile`  

---

## Instalación

1. **Clona este repositorio**:
   ```bash
   git clone https://github.com/adrianArimany/proyectos_AED.git
   cd proyectos_AED/proyecto2-python 
   ```
2. **Crea y activa un entorno virtual** (se asume Windows/PowerShell; en Linux/macOS basta con `python3 -m venv .venv` y luego `source .venv/bin/activate`):
   ```powershell
   python -m venv .venv
   Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass  # (solo si PowerShell lo bloquea)
   .\.venv\Scripts\Activate.ps1
   ```
3. **Instala las dependencias**:
   ```bash
   pip install --upgrade pip
   pip install -r requirements.txt
   ```

---

## Variables de configuración

Esta es la configuracion para acceder a Neo4j Aura:

```python
# config.py
AURA_URI          = "neo4j+s://39e71d3b.databases.neo4j.io:7687"
AURA_USER         = "neo4j"
AURA_PASS         = "oW-zD88F8HYM-ML9SkEdvnCGIG8HES8BsyJK6yLTv-w"


NEEDED_PER_FAMILY = 2   # En el caso que no tenga suficiente memoria, puede limir la base de datos.
#Esta info se encuentra en:
#data/Neo4j-39e71d3b-Created-2025-05-28.txt 
```

---

## Cómo ejecutar

Con el entorno virtual activo y las dependencias instaladas, ejecuta:
```bash
streamlit run app.py
```
Streamlit levantará un servidor local (por defecto en http://localhost:8501).  
Abre esa URL en tu navegador para ver la interfaz.

---

## Descripción de archivos

- **`config.py`**  
  Contiene constantes de configuración:
  - `AURA_URI`, `AURA_USER`, `AURA_PASS`: datos de conexión a Neo4j Aura.
  - `NEEDED_PER_FAMILY`: cuántas muestras seleccionar por familia (opcional).

- **`db.py`**  
  Inicializa el driver de Neo4j y define funciones para:
  - `ensure_sample_nodes(df)`: escribe/actualiza todos los nodos `:Sample` en la base a partir de un DataFrame.
  - `create_user(name, expert)`: crea o busca un nodo `:Expert {name}` o `:Inexpert {name}`.
  - `record_preference(expert_name, sample_id)`: graba la relación `(Expert)-[:PREFERS]->(Sample)`.
  - `fetch_expert_recs()`: retorna todos los `Sample` preferidos por expertos.
  - `record_download(inexpert_name, sample_id, hit)`: crea `(Inexpert)-[DOWNLOADS {hit: True/False}]->(Sample)`.
  - `get_success_stats(inexpert_name)`: devuelve `(hits, total)` descargas con aciertos.

- **`ingestion.py`**  
  Contiene funciones para cargar metadatos NSynth desde Hugging Face (sin audio):
  - `ingest_nsynth_hf(full_split: bool) -> DataFrame`: si `full_split=True`, combina “train”, “validation” y “test”; si `False`, solo “test”. Elimina la columna “audio” para evitar dependencias de decodificación de sonido.
  - `get_samples_df(source="hf", csv_path=None, full=False) -> DataFrame`: delega a `ingest_nsynth_hf()` o a un futuro `ingest_csv()` si hubiera un CSV local.

- **`filters.py`**  
  Contiene la lógica de filtrado puro (sin Streamlit) sobre DataFrames:
  - `apply_expert_filters(df, pitch_range, vel_range, sr_sel, fam_sel, inst_sel, qual_sel) -> DataFrame`: filtra “sample_df” por rango de pitch, velocity, sample_rate, familia, instrumento y calidades.
  - `apply_inexpert_filters(recs_df, fam_sel_in, vel_range_in, sr_sel_in, qual_sel_in) -> DataFrame`: filtra “recs_df” (samples ya preferidos por expertos) según familia, velocity, sample_rate y calidades seleccionadas.

- **`scoring.py`**  
  Contiene la función para calcular métricas de acierto:
  - `compute_success_stats(inexpert_name: str) -> Tuple[int,int,float]`: obtiene `(hits, total)` desde `db.get_success_stats(...)` y calcula la tasa `hits/total`.

- **`app.py`**  
  Script principal que define la interfaz de Streamlit:
  1. Ingiere todas las muestras desde `ingestion.get_samples_df(full=True)` y las escribe a Neo4j con `db.ensure_sample_nodes(...)`.
  2. Pide nombre de usuario y tipo (Experto/Inexperto) y crea el nodo correspondiente en Neo4j.
  3. **Modo Experto**:  
     - Muestra sliders y selectores (pitch, velocity, sample_rate, familia, instrumento, calidades).  
     - Llama a `filters.apply_expert_filters(...)` para mostrar coincidencias.  
     - Al hacer clic en “Download <sample_id>”, graba `db.record_preference(...)`.
  4. **Modo Inexperto**:  
     - Obtiene todos los samples preferidos por expertos con `db.fetch_expert_recs()` (en DataFrame).  
     - Muestra filtros simplificados (familia, velocity, sample_rate, calidades).  
     - Llama a `filters.apply_inexpert_filters(...)` para mostrar recomendaciones.  
     - Al descargar, comprueba si `id` está en la lista de expertos (`hit`), graba con `db.record_download(...)` y muestra un mensaje.  
     - Muestra la métrica `Success rate` usando `scoring.compute_success_stats(...)`.

- **`requirements.txt`**  
  Lista las dependencias que debe instalarse con `pip install -r requirements.txt`:
  ```
  streamlit
  pandas
  datasets
  neo4j
  librosa
  soundfile
  ```

---

## Resumen del flujo

1. **Preparación**  
   - Clona el repositorio, crea y activa el entorno virtual, instala dependencias.  
   - Rellena `config.py` con tu URI/usuario/contraseña de Neo4j Aura.

2. **Ejecución**  
   ```bash
   streamlit run app.py
   ```
3. **Interacción**  
   - El sistema ingiere todos los metadatos NSynth y los guarda como nodos `:Sample`.  
   - Usuario se identifica como “Experto” (filtra y marca preferencias) o “Inexperto” (filtra solo entre las muestras aprobadas por expertos).  
   - Los clicks de descarga generan relaciones en la base de datos y la sección Inexpert muestra la tasa de aciertos acumulada.

