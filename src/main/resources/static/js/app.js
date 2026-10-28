/**
 * BU Finder - Lost and Found Application
 * Client-Side JavaScript for dynamic cascading dropdowns, file preview, and interactions.
 */

document.addEventListener('DOMContentLoaded', function () {
    // 1. Dynamic Cascading Category -> Subtype & Brand
    const categorySelect = document.getElementById('categorySelect');
    const subTypeSelect = document.getElementById('subTypeSelect');
    const brandSelect = document.getElementById('brandSelect');

    if (categorySelect && (subTypeSelect || brandSelect)) {
        categorySelect.addEventListener('change', function () {
            const selectedCategory = this.value;
            updateSubTypesAndBrands(selectedCategory);
        });

        // Initialize if already selected (e.g. on validation error or edit page)
        if (categorySelect.value && subTypeSelect && subTypeSelect.options.length <= 1) {
            updateSubTypesAndBrands(categorySelect.value);
        }
    }

    function updateSubTypesAndBrands(category) {
        if (!category) return;

        // Fetch sub-types
        if (subTypeSelect) {
            const currentSubVal = subTypeSelect.getAttribute('data-selected') || subTypeSelect.value;
            fetch('/api/categories/subtypes?category=' + encodeURIComponent(category))
                .then(res => res.json())
                .then(items => {
                    subTypeSelect.innerHTML = '<option value="">-- Select Sub-type --</option>';
                    items.forEach(item => {
                        const opt = document.createElement('option');
                        opt.value = item;
                        opt.textContent = item;
                        if (item === currentSubVal) {
                            opt.selected = true;
                        }
                        subTypeSelect.appendChild(opt);
                    });
                })
                .catch(err => console.error('Error fetching sub-types:', err));
        }

        // Fetch brands
        if (brandSelect) {
            const currentBrandVal = brandSelect.getAttribute('data-selected') || brandSelect.value;
            fetch('/api/categories/brands?category=' + encodeURIComponent(category))
                .then(res => res.json())
                .then(items => {
                    brandSelect.innerHTML = '<option value="">-- Select Brand --</option>';
                    items.forEach(brand => {
                        const opt = document.createElement('option');
                        opt.value = brand;
                        opt.textContent = brand;
                        if (brand === currentBrandVal) {
                            opt.selected = true;
                        }
                        brandSelect.appendChild(opt);
                    });
                })
                .catch(err => console.error('Error fetching brands:', err));
        }
    }

    // 2. Image upload preview
    const imageInput = document.getElementById('imageFile');
    const previewContainer = document.getElementById('imagePreviewContainer');
    const previewImage = document.getElementById('imagePreview');

    if (imageInput && previewContainer && previewImage) {
        imageInput.addEventListener('change', function () {
            const file = this.files[0];
            if (file) {
                // Check size (5MB max)
                if (file.size > 5 * 1024 * 1024) {
                    alert('File size exceeds 5MB limit. Please choose a smaller image.');
                    this.value = '';
                    previewContainer.style.display = 'none';
                    return;
                }

                const reader = new FileReader();
                reader.onload = function (e) {
                    previewImage.src = e.target.result;
                    previewContainer.style.display = 'block';
                };
                reader.readAsDataURL(file);
            } else {
                previewContainer.style.display = 'none';
            }
        });
    }

    // 3. Auto-dismiss flash alerts after 6 seconds
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            if (bsAlert) {
                bsAlert.close();
            }
        }, 6000);
    });
});
