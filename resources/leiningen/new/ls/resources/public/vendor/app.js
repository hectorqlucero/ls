$(document).ready(function () {
  // Global flag for form submission tracking
  var isFormSubmitting = false;
  // Global flag to control subgrid modal closing
  var allowSubgridModalClose = false;

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
  });  // Form submit with validation
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
    var subgridModal = $('#subgridModal');

    // Check if we're submitting a form while subgrid modal is open
    var isSubgridModalOpen = subgridModal.length > 0 && subgridModal.hasClass('show') && subgridModal.is(':visible');

    // Robust subgrid context detection - check multiple sources
    var modalSubgridUrl = subgridModal.data('current-subgrid-url');
    var modalParentId = subgridModal.data('parent-id');
    var localSubgridUrl = localStorage.getItem('current-subgrid-url');
    var localParentId = localStorage.getItem('parent-id');
    var isSubgridModalVisible = subgridModal.length > 0 && subgridModal.hasClass('show');

    // Prefer modal data, fallback to localStorage
    var subgridUrl = modalSubgridUrl || localSubgridUrl;
    var parentId = modalParentId || localParentId;

    // Context is subgrid if we have both URL and parent ID, AND the subgrid modal is visible
    var isSubgridContext = !!(subgridUrl && parentId && isSubgridModalVisible);

    // Fallback detection: if subgrid modal is visible, treat as subgrid context
    if (!isSubgridContext && isSubgridModalOpen) {
      isSubgridContext = true;
      // Try to get URL and parent ID from anywhere we can
      if (!subgridUrl) {
        subgridUrl = localStorage.getItem('current-subgrid-url');
      }
      if (!parentId) {
        parentId = localStorage.getItem('parent-id');
      }
    }

    // Set flag to prevent subgrid cleanup during form submission
    if (isSubgridContext) {
      isFormSubmitting = true;
    }

    $.post($(this).attr('action'), data, function (response) {
      if (isSubgridContext) {
        if (subgridUrl && parentId) {
          subgridModal.find('#subgrid-content').html(`
            <div class='text-center p-4'>
              <div class='spinner-border text-primary' role='status'></div>
              <div class='mt-2'>Refreshing...</div>
            </div>
          `);

          // Use the URL as-is since it already contains the parent_id parameter
          $.get(subgridUrl, function (newData) {
            if (newData.trim() === '') {
              subgridModal.find('#subgrid-content').html(
                "<div class='alert alert-warning'>No data available for the subgrid. Please contact support.</div>"
              );
            } else {
              subgridModal.find('#subgrid-content').html(newData);

              // Re-initialize DataTable for the refreshed subgrid
              var subTable = subgridModal.find('table.dataTable');
              var allTables = subgridModal.find('table');

              // Destroy existing DataTable if it exists
              if (subTable.length > 0 && $.fn.DataTable.isDataTable(subTable[0])) {
                subTable.DataTable().destroy();
                // Remove DataTable classes that might interfere
                subTable.removeClass('dataTable');
              }

              // Re-find the table after destruction
              var tableToInit = subgridModal.find('table');
              if (tableToInit.length > 0) {
                // Add dataTable class if not present
                if (!tableToInit.hasClass('dataTable')) {
                  tableToInit.addClass('dataTable');
                }

                try {
                  // Wait a moment for DOM to be fully ready
                  setTimeout(function () {
                    tableToInit.DataTable({
                      responsive: true,
                      pageLength: 10,
                      destroy: true, // Allow re-initialization
                      language: {
                        search: 'Search:',
                        lengthMenu: 'Show _MENU_ entries',
                        info: 'Showing _START_ to _END_ of _TOTAL_ entries',
                        emptyTable: 'No data available in table',
                        paginate: {
                          first: 'First',
                          last: 'Last',
                          next: 'Next',
                          previous: 'Previous'
                        }
                      }
                    });
                  }, 100);
                } catch (error) {
                  // Silent fail - DataTable initialization issues are non-critical
                }
              }
            }
          }).fail(function () {
            subgridModal.find('#subgrid-content').html(
              "<div class='alert alert-danger'>Error refreshing subgrid content. Please contact support.</div>"
            );
          });
        } else {
          subgridModal.find('#subgrid-content').html(
            "<div class='alert alert-danger'>Error: Missing subgrid attributes. Please contact support.</div>"
          );
        }

        // Close the form modal and ensure subgrid modal stays visible
        $('#exampleModal').modal('hide');

        // Ensure subgrid modal remains visible
        if (!subgridModal.hasClass('show')) {
          subgridModal.addClass('show').css('display', 'block');
        }
      } else {
        location.reload();
      }
    }).fail(function () {
      alert('Error saving record. Please try again.');
    });
  });

  // Confirm before deleting a record
  $(document).on('click', '.btn-danger', function (e) {
    // Only act if this is a delete button inside a table row
    if ($(this).closest('table').length && $(this).text().toLowerCase().includes('delete')) {
      e.preventDefault();
      var url = $(this).attr('href');
      var subgridModal = $('#subgridModal');
      var isSubgridContext = subgridModal.length > 0 && subgridModal.is(':visible') && subgridModal.hasClass('show');

      if (confirm('Are you sure you want to delete this record?')) {
        if (isSubgridContext) {
          // Handle delete in subgrid context - use AJAX and refresh subgrid
          // Delete in subgrid context - use AJAX
          $.get(url, function (response) {
            // Refresh the subgrid content after deletion
            var currentUrl = subgridModal.data('current-subgrid-url') || localStorage.getItem('current-subgrid-url');
            var parentId = subgridModal.data('parent-id') || localStorage.getItem('parent-id');

            if (currentUrl && parentId) {
              subgridModal.find('#subgrid-content').html(`
                <div class='text-center p-4'>
                  <div class='spinner-border text-primary' role='status'></div>
                  <div class='mt-2'>Refreshing...</div>
                </div>
              `);

              $.get(currentUrl, { parent_id: parentId }, function (newData) {
                subgridModal.find('#subgrid-content').html(newData);

                // Re-initialize DataTable for the refreshed subgrid after delete
                var subTable = subgridModal.find('table.dataTable');
                var allTables = subgridModal.find('table');

                // Destroy existing DataTable if it exists
                if (subTable.length > 0 && $.fn.DataTable.isDataTable(subTable[0])) {
                  subTable.DataTable().destroy();
                  subTable.removeClass('dataTable');
                }

                // Re-find the table after destruction
                var tableToInit = subgridModal.find('table');
                if (tableToInit.length > 0) {
                  // Add dataTable class if not present
                  if (!tableToInit.hasClass('dataTable')) {
                    tableToInit.addClass('dataTable');
                  }

                  try {
                    // Wait a moment for DOM to be fully ready
                    setTimeout(function () {
                      tableToInit.DataTable({
                        responsive: true,
                        pageLength: 10,
                        destroy: true, // Allow re-initialization
                        language: {
                          search: 'Search:',
                          lengthMenu: 'Show _MENU_ entries',
                          info: 'Showing _START_ to _END_ of _TOTAL_ entries',
                          emptyTable: 'No data available in table',
                          paginate: {
                            first: 'First',
                            last: 'Last',
                            next: 'Next',
                            previous: 'Previous'
                          }
                        }
                      });
                    }, 100);
                  } catch (error) {
                    // Silent fail - DataTable initialization issues are non-critical
                  }
                }
              }).fail(function () {
                subgridModal.find('#subgrid-content').html(
                  "<div class='alert alert-danger'>Error refreshing subgrid content. Please contact support.</div>"
                );
              });
            } else {
              // Missing subgrid attributes - no action needed
            }
          }).fail(function () {
            alert('Error deleting record. Please try again.');
          });
        } else {
          // Normal context - navigate to delete URL
          window.location.href = url;
        }
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

  // Ensure subgrid modal attributes are set dynamically and persist
  $(document).on('click', '[data-subgrid-url]', function (e) {
    e.preventDefault();
    var subgridUrl = $(this).data('subgrid-url');
    var parentId = $(this).data('parent-id');
    var subgridModal = $('#subgridModal');
    if (!subgridUrl) {
      subgridModal.find('#subgrid-content').html(
        "<div class='alert alert-danger'>Error: Subgrid URL is missing. Please contact support.</div>"
      );
      return;
    }

    // Set modal attributes dynamically
    subgridModal.data('current-subgrid-url', subgridUrl);
    localStorage.setItem('current-subgrid-url', subgridUrl);

    if (parentId) {
      subgridModal.data('parent-id', parentId);
      localStorage.setItem('parent-id', parentId);
    } else {
      parentId = localStorage.getItem('parent-id');
      if (parentId) {
        subgridModal.data('parent-id', parentId);
      } else {
        subgridModal.find('#subgrid-content').html(
          "<div class='alert alert-danger'>Error: Parent ID is missing. Please contact support.</div>"
        );
        return;
      }
    }

    // Load subgrid content
    subgridModal.find('#subgrid-content').html(
      "<div class='text-center p-4'><div class='spinner-border text-primary' role='status'></div></div>"
    );

    // Use the URL as-is since it already contains the parent_id parameter
    $.get(subgridUrl, function (data) {
      subgridModal.find('#subgrid-content').html(data);

      // Force modal to stay open after content load
      setTimeout(function () {
        if (!subgridModal.hasClass('show')) {
          subgridModal.addClass('show').css('display', 'block');
        }
      }, 100);

      // Initialize DataTable if present
      var subTable = subgridModal.find('table.dataTable');
      var allTables = subgridModal.find('table');

      if (subTable.length > 0 && !$.fn.DataTable.isDataTable(subTable[0])) {
        try {
          // Wait a moment for DOM to be fully ready
          setTimeout(function () {
            subTable.DataTable({
              responsive: true,
              pageLength: 10,
              destroy: true, // Allow re-initialization
              language: {
                search: 'Search:',
                lengthMenu: 'Show _MENU_ entries',
                info: 'Showing _START_ to _END_ of _TOTAL_ entries',
                emptyTable: 'No data available in table',
                paginate: {
                  first: 'First',
                  last: 'Last',
                  next: 'Next',
                  previous: 'Previous'
                }
              }
            });
          }, 100);
        } catch (error) {
          // Silent fail - DataTable initialization issues are non-critical
          // Fallback: try without responsive
          try {
            setTimeout(function () {
              subTable.DataTable({
                pageLength: 10,
                destroy: true,
                language: {
                  search: 'Search:',
                  lengthMenu: 'Show _MENU_ entries',
                  info: 'Showing _START_ to _END_ of _TOTAL_ entries',
                  emptyTable: 'No data available in table'
                }
              });
            }, 200);
          } catch (fallbackError) {
            // Silent fail for fallback as well
          }
        }
      }
    }).fail(function (xhr, status, error) {
      subgridModal.find('#subgrid-content').html(
        "<div class='alert alert-danger'>Error loading subgrid content. Status: " + xhr.status + ". Please contact support.</div>"
      );
    });

    subgridModal.modal('show');
  });

  // Modal event handlers
  $(document).on('hide.bs.modal', '#subgridModal', function (e) {
    // Prevent automatic hiding unless explicitly allowed
    if (!allowSubgridModalClose && !e.relatedTarget && !isFormSubmitting) {
      e.preventDefault();
      e.stopPropagation();
      return false;
    }
  });

  // Clear subgrid data when subgrid modal is manually closed (not during form operations)
  $(document).on('hidden.bs.modal', '#subgridModal', function (e) {

    // Reset the close flag
    allowSubgridModalClose = false;

    // Safely destroy any DataTable in the subgrid to prevent conflicts
    try {
      var subgridModal = $(this);
      var subTable = subgridModal.find('table');
      if (subTable.length > 0) {
        subTable.each(function () {
          if ($.fn.DataTable.isDataTable(this)) {
            $(this).DataTable().destroy(true); // true = remove all DataTable elements
          }
        });
      }
    } catch (error) {
      // Silent fail for DataTable cleanup errors
    }

    // Add a small delay to allow for other operations to complete
    setTimeout(function () {
      if (!isFormSubmitting) {
        var subgridModal = $('#subgridModal');
        subgridModal.removeData('current-subgrid-url');
        subgridModal.removeData('parent-id');
        localStorage.removeItem('current-subgrid-url');
        localStorage.removeItem('parent-id');
      }
    }, 100);
  });

  // Reset the form submitting flag when the form modal is closed
  $(document).on('hidden.bs.modal', '#exampleModal', function () {
    setTimeout(function () {
      isFormSubmitting = false;
    }, 200);
  });

  // Allow manual closing of subgrid modal when user clicks close button
  $(document).on('click', '#subgridModal .btn-close, #subgridModal [data-bs-dismiss="modal"]', function () {
    allowSubgridModalClose = true;
    $('#subgridModal').modal('hide');
  });
});
