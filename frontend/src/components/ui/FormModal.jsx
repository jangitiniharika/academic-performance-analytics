import { useState } from 'react';

import Modal from './Modal.jsx';

/**
 * A small, configuration-driven form inside a modal.
 *
 * `fields` is an array of:
 *   { name, label, type: 'text'|'email'|'number'|'date'|'select'|'textarea',
 *     required?, options?: [{ value, label }], placeholder?, step?, parse? }
 *
 * `parse` converts the raw input to the value the API expects (for example
 * turning a numeric string into a Number).
 */
export default function FormModal({
  title,
  fields,
  initialValues = {},
  submitLabel = 'Save',
  onSubmit,
  onClose,
}) {
  const [values, setValues] = useState(() => {
    const start = {};
    fields.forEach((field) => {
      start[field.name] = initialValues[field.name] ?? field.defaultValue ?? '';
    });
    return start;
  });
  const [errors, setErrors] = useState({});
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState(null);

  const setValue = (name, value) => setValues((previous) => ({ ...previous, [name]: value }));

  async function handleSubmit(event) {
    event.preventDefault();

    const validation = {};
    fields.forEach((field) => {
      const value = values[field.name];
      if (field.required && (value === '' || value === null || value === undefined)) {
        validation[field.name] = `${field.label} is required.`;
      }
    });
    setErrors(validation);
    if (Object.keys(validation).length > 0) {
      return;
    }

    setSaving(true);
    setFormError(null);
    try {
      const payload = {};
      fields.forEach((field) => {
        const raw = values[field.name];
        payload[field.name] = field.parse ? field.parse(raw) : raw;
      });
      await onSubmit(payload);
    } catch (error) {
      setFormError(error.message || 'Could not save the record.');
      if (error.fieldErrors) {
        setErrors(error.fieldErrors);
      }
      setSaving(false);
    }
  }

  return (
    <Modal title={title} onClose={onClose}>
      <form className="form" onSubmit={handleSubmit} noValidate>
        {formError && <div className="banner banner--error">{formError}</div>}
        {fields.map((field) => (
          <label key={field.name} className="form__field">
            <span className="form__label">
              {field.label}
              {field.required && <span className="form__required">*</span>}
            </span>
            {renderControl(field, values[field.name] ?? '', (value) => setValue(field.name, value))}
            {errors[field.name] && <span className="form__error">{errors[field.name]}</span>}
          </label>
        ))}
        <div className="form__actions">
          <button type="button" className="btn btn--ghost" onClick={onClose} disabled={saving}>
            Cancel
          </button>
          <button type="submit" className="btn btn--primary" disabled={saving}>
            {saving ? 'Saving…' : submitLabel}
          </button>
        </div>
      </form>
    </Modal>
  );
}

function renderControl(field, value, onChange) {
  const common = {
    id: field.name,
    name: field.name,
    className: 'input',
    value,
    onChange: (event) => onChange(event.target.value),
  };

  if (field.type === 'select') {
    return (
      <select {...common}>
        <option value="">Select…</option>
        {(field.options ?? []).map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
    );
  }
  if (field.type === 'textarea') {
    return <textarea {...common} rows={3} />;
  }
  return (
    <input
      {...common}
      type={field.type ?? 'text'}
      step={field.step}
      placeholder={field.placeholder}
    />
  );
}
