$(document).ready(function () {
  var table = $('.dataTable').DataTable({
    stateSave: true,
    responsive: true,
    autoWidth: false,
    paging: true,
    pageLength: 10, // default
    lengthChange: true, // allow user to change
    lengthMenu: [[5, 10, 25, 50, 100], [5, 10, 25, 50, 100]], // user options
    searching: true,
    info: true,
    ordering: true,
    dom: "<'row mb-3'<'col-sm-12 col-md-4'l><'col-sm-12 col-md-4'B><'col-sm-12 col-md-4'f>>" +
      "<'row'<'col-sm-12'tr>>" +
      "<'row mt-3'<'col-sm-12 col-md-5'i><'col-sm-12 col-md-7'p>>",
    buttons: [
      { extend: 'copy', className: 'btn btn-primary btn-sm' },
      { extend: 'csv', className: 'btn btn-primary btn-sm' },
      { extend: 'excel', className: 'btn btn-primary btn-sm' },
      { extend: 'pdf', className: 'btn btn-primary btn-sm' },
      { extend: 'print', className: 'btn btn-primary btn-sm' }
    ],
    language: {
      search: '_INPUT_',
      searchPlaceholder: 'Search...',
      lengthMenu: 'Show _MENU_ entries',
      info: 'Showing _START_ to _END_ of _TOTAL_ entries',
      paginate: {
        previous: '<i class="bi bi-chevron-left"></i>',
        next: '<i class="bi bi-chevron-right"></i>'
      }
    }
  });

  // Style the page length selector with Bootstrap classes
  $('.dataTables_length select').addClass('form-select form-select-sm bg-light border-0 shadow-sm');

  // Go to last page if a new record was just added
  if (localStorage.getItem('datatable_goto_last') === '1') {
    table.page('last').draw('page');
    localStorage.removeItem('datatable_goto_last');
  }

  // Modal AJAX logic
  $(document).on('click', '.new-record-btn, .edit-record-btn', function (e) {
    e.preventDefault();
    var url = $(this).data('url');
    var modal = $('#exampleModal');
    modal.find('.modal-title').text($(this).hasClass('new-record-btn') ? 'New Record' : 'Edit Record');
    modal.find('.modal-body').html('<div class="text-center p-4"><div class="spinner-border text-primary" role="status"></div></div>');
    $.get(url, function (data) {
      modal.find('.modal-body').html(data);
    });
    modal.modal('show');
  });

  // Form submit with validation
  $(document).on('submit', '#exampleModal form', function (e) {
    e.preventDefault();
    var $form = $(this)[0];

    if (!$form.checkValidity()) {
      $form.reportValidity();
      return false;
    }

    // Detect new record (no id or empty id)
    var isNew = !$($form).find('[name="id"]').val();
    if (isNew) {
      localStorage.setItem('datatable_goto_last', '1');
    }

    var data = $(this).serialize();
    $.post($(this).attr('action'), data, function (response) {
      $('#exampleModal').modal('hide');
      location.reload(); // DataTables will restore state, and our code will jump to last page if needed
    });
  });

  // Confirm before deleting a record
  $(document).on('click', '.btn-danger', function (e) {
    // Only act if this is a delete button inside a table row
    if ($(this).closest('table').length && $(this).text().toLowerCase().includes('delete')) {
      e.preventDefault();
      var url = $(this).attr('href');
      if (confirm('Are you sure you want to delete this record?')) {
        // Proceed with deletion
        window.location.href = url;
      }
    }
  });

  // Highlight nav-link and dropdown-toggle on click
  $(document).on('click', '.nav-link', function (e) {
    // Remove highlight from all nav links
    document.querySelectorAll('.nav-link').forEach(function (el) {
      el.classList.remove('active', 'bg-gradient', 'text-primary-emphasis', 'shadow-sm');
    });

    // Add highlight to the clicked nav-link or dropdown-toggle
    this.classList.add('active', 'bg-gradient', 'text-primary-emphasis', 'shadow-sm');
    localStorage.setItem('active-link', this.dataset.id);

    if ($(this).hasClass('dropdown-item')) {
      var parentToggle = $(this).closest('.dropdown').find('.dropdown-toggle')[0];
      if (parentToggle) {
        parentToggle.classList.add('active', 'bg-gradient', 'text-primary-emphasis', 'shadow-sm');
        localStorage.setItem('active-dropdown-parent', parentToggle.dataset.id);
      }
    } else if ($(this).hasClass('dropdown-toggle')) {
      // If clicking the dropdown parent itself, store as both active-link and active-dropdown-parent
      localStorage.setItem('active-dropdown-parent', this.dataset.id);
    } else {
      // If not a dropdown item or toggle, clear the dropdown parent highlight
      localStorage.removeItem('active-dropdown-parent');
    }
  });

  // Restore highlight from localStorage on page load
  var activeId = localStorage.getItem('active-link');
  var activeDropdownParent = localStorage.getItem('active-dropdown-parent');
  document.querySelectorAll('.nav-link').forEach(function (el) {
    el.classList.remove('active', 'bg-gradient', 'text-primary-emphasis', 'shadow-sm');
  });
  if (activeId) {
    var navLink = document.querySelector('.nav-link[data-id="' + activeId + '"]');
    if (navLink) {
      navLink.classList.add('active', 'bg-gradient', 'text-primary-emphasis', 'shadow-sm');
    }
  }
  if (activeDropdownParent) {
    var parentToggle = document.querySelector('.nav-link.dropdown-toggle[data-id="' + activeDropdownParent + '"]');
    if (parentToggle) {
      parentToggle.classList.add('active', 'bg-gradient', 'text-primary-emphasis', 'shadow-sm');
    }
  }
});
