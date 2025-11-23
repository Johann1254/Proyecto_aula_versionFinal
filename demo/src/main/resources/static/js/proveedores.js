const apiUrl = "http://localhost:8080/api/proveedores";

$(document).ready(function () {
  $("#dataTable").DataTable({
    serverSide: true,
    processing: true,
    responsive: true,
    autoWidth: false,
    paging: true,
    ordering: true,
    searching: true,
    pageLength: 5,
    lengthMenu: [5, 20, 50, 100],
    language: {
      search: "Buscar:",
      lengthMenu: "Mostrar _MENU_ registros",
      info: "Mostrando _START_ a _END_ de _TOTAL_ proveedores",
      paginate: {
        first: "Primero",
        last: "Último",
        next: "Siguiente",
        previous: "Anterior",
      },
    },

    ajax: async function (data, callback) {
      try {
        const page = Math.floor(data.start / data.length);
        const size = data.length;

        const response = await fetch(`${apiUrl}?page=${page}&size=${size}`);
        if (!response.ok) {
          throw new Error(`Error al obtener proveedores: ${response.status}`);
        }

        const json = await response.json();

        const proveedores = json.data || json.content || json || [];

        const formattedData = proveedores.map((p) => {
          return {
            nombre: p.nombre,
            telefono: p.telefono,
            nit: p.nit,
            direccion: p.direccion,
            acciones: `
      <a href="/proveedores/editar?id=${p.id}" class="btn btn-primary btn-circle">
        <i class="fas fa-edit"></i>
      </a>
      <button class="btn btn-danger btn-circle btn-eliminar" data-id="${p.id}">
        <i class="fas fa-trash"></i>
      </button>
    `,
          };
        });

        // Respuesta hacia DataTables
        callback({
          draw: data.draw,
          recordsTotal: json.recordsTotal,
          recordsFiltered: json.recordsFiltered,
          data: formattedData,
        });
      } catch (error) {
        console.error("Error al obtener proveedores:", error);
      }
    },

    columns: [
      { data: "nombre", title: "Nombre" },
      { data: "telefono", title: "Teléfono" },
      { data: "nit", title: "NIT" },
      { data: "direccion", title: "Dirección" },
      {
        data: "acciones",
        title: "Acciones",
        orderable: false,
        searchable: false,
      },
    ],
  });
});
