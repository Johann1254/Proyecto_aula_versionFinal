// ================================
//  CONFIG
// ================================
const apiColegios = "http://localhost:8080/api/colegios";
const apiCalculadora = "http://localhost:8080/api/calculadora";

const selectColegio = document.getElementById("selectColegio");
const selectUniforme = document.getElementById("selectUniforme");
const resultadoDiv = document.getElementById("resultado");
const btnVerificar = document.getElementById("verificar");


// ================================
//  1️⃣ Cargar colegios
// ================================
async function cargarColegios() {
    try {
        const res = await fetch(apiColegios);
        const colegios = await res.json();

        selectColegio.innerHTML =
            '<option value="">Seleccione un colegio</option>';

        colegios.forEach(colegio => {
            const option = document.createElement("option");
            option.value = colegio.id;
            option.textContent = colegio.nombre;
            selectColegio.appendChild(option);
            selectUniforme.innerHTML =
            '<option value="">Seleccione un uniforme</option>';
        });

    } catch (error) {
        console.error("Error cargando colegios:", error);
        mostrarError("No se pudieron cargar los colegios.");
    }
}


// ================================
//  2️⃣ Cargar uniformes según colegio
// ================================
selectColegio.addEventListener("change", async () => {
    const idColegio = selectColegio.value;

    selectUniforme.innerHTML = "";
    resultadoDiv.innerHTML = ""; // limpiar resultado

    if (!idColegio) return;

    try {
        const res = await fetch(`${apiColegios}/${idColegio}/uniformes`);
        const data = await res.json();

        if (!Array.isArray(data)) {
            selectUniforme.innerHTML =
                `<option value="">${data.mensaje || "Sin uniformes"}</option>`;
            return;
        }

        selectUniforme.innerHTML =
            '<option value="">Seleccione un uniforme</option>';

        data.forEach(u => {
            const option = document.createElement("option");
            option.value = u.id;
            option.textContent = `${u.prenda} - ${u.tipo} (${u.talla}, ${u.genero})`;
            selectUniforme.appendChild(option);
        });

    } catch (error) {
        console.error("Error cargando uniformes:", error);
        mostrarError("Error cargando uniformes.");
    }
});


// ================================
//  3️⃣ Verificar disponibilidad
// ================================
btnVerificar.addEventListener("click", async () => {
    const idUniforme = selectUniforme.value;
    const cantidad = document.getElementById("cantidadPedido").value;

    if (!idUniforme || !cantidad) {
        mostrarError("Debe seleccionar un uniforme y una cantidad válida.");
        return;
    }

    try {
        const res = await fetch(
            `${apiCalculadora}/verificar?idUniforme=${idUniforme}&cantidad=${cantidad}`
        );

        const data = await res.json();

        if (data.error) {
            mostrarError(data.error);
            return;
        }

        mostrarResultado(data);

    } catch (error) {
        console.error("Error verificando disponibilidad:", error);
        mostrarError("Error verificando disponibilidad.");
    }
});


// ================================
//  Funciones de UI
// ================================
function mostrarError(msg) {
    resultadoDiv.innerHTML = `
        <div class="alert alert-danger mt-3">${msg}</div>
    `;
}

function mostrarResultado(data) {

    const detallesHTML = data.detalles.map(d => `
        <tr>
            <td>${d.insumo}</td>
            <td>${d.estado}</td>
            <td>${d.stockActual}</td>
            <td>${d.stockNecesario}</td>
            <td>${d.stockRestante}</td>
        </tr>
    `).join("");

    resultadoDiv.innerHTML = `
        <div class="card shadow mt-3 p-3 fade-in">

            <h5 class="fw-bold mb-1">${data.uniforme}</h5>
            <p class="mb-0"><strong>Cantidad:</strong> ${data.cantidad}</p>
            <p class="mb-2">
                <strong>Disponibilidad:</strong> 
                ${data.disponible ? "✅ Suficiente" : "⚠️ Insuficiente"}
            </p>

            <table class="table table-bordered table-sm mt-3">
                <thead class="table-light">
                    <tr>
                        <th>Insumo</th>
                        <th>Estado</th>
                        <th>Stock Actual</th>
                        <th>Stock Necesario</th>
                        <th>Stock Restante</th>
                    </tr>
                </thead>
                <tbody>${detallesHTML}</tbody>
            </table>

        </div>
    `;
}

// ================================
//  Inicializar
// ================================
cargarColegios();
