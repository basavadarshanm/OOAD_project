# 🎨 CSS & Styling Improvements Guide

## Overview
This document outlines all the visual and styling improvements made to the Premium Banking System application.

---

## 📊 Color Palette

### Primary Colors
```
Primary Blue:        #0066cc  (Blue for buttons, links)
Dark Blue:           #1e3c72  (Header background)
Gradient Blue:       #2a5298  (Header end gradient)
Secondary Blue:      #0052a3  (Hover state)
Success Green:       #27ae60  (Positive actions)
Danger Red:          #e74c3c  (Alerts, errors)
Warning Orange:      #f39c12  (Warnings)
Info Cyan:           #3498db  (Information)
```

### Secondary Colors
```
Light Gray:          #f0f3f7  (Background)
Medium Gray:         #666666  (Text labels)
Dark Gray:           #1a1a1a  (Main text)
Border Gray:         #c5cad1  (Borders)
```

---

## 🎯 CSS Classes Reference

### Buttons

#### Primary Action Button
```css
.action-button {
    -fx-background-color: linear-gradient(to bottom, #0073E6, #0052a3);
    -fx-text-fill: white;
    -fx-padding: 12px 20px;
    -fx-border-radius: 6px;
}
```
**Use Case**: Main submit buttons, "Send Transfer", "Pay Bill"

#### Secondary Button
```css
.button-secondary {
    -fx-background-color: #e3e8f1;
    -fx-text-fill: #0066cc;
    -fx-border-color: #c5cad1;
}
```
**Use Case**: Alternative actions, "Back to Login"

#### Danger Button
```css
.button-danger {
    -fx-background-color: linear-gradient(to bottom, #e74c3c, #c0392b);
    -fx-text-fill: white;
}
```
**Use Case**: Delete, Block card actions

#### Success Button
```css
.button-success {
    -fx-background-color: linear-gradient(to bottom, #27ae60, #1e8449);
    -fx-text-fill: white;
}
```
**Use Case**: Confirm, Approve actions

#### Navigation Button
```css
.nav-button {
    -fx-padding: 10px 16px;
    -fx-background-color: rgba(255, 255, 255, 0.15);
    -fx-text-fill: white;
}
```
**Use Case**: Header buttons (Refresh, Logout)

---

### Input Fields

#### Text Field (Standard)
```css
.text-field {
    -fx-padding: 12px;
    -fx-border-color: #c5cad1;
    -fx-border-radius: 6px;
    -fx-font-size: 13px;
    -fx-control-inner-background: #fafbfc;
}

.text-field:focused {
    -fx-border-color: #0066cc;
    -fx-border-width: 2;
    -fx-style-shadow: dropshadow(gaussian, rgba(0, 102, 204, 0.25), 6, 0, 0, 2);
}
```
**Use Case**: Regular form inputs

#### Login Input Field
```css
.login-input {
    -fx-padding: 14px 16px;
    -fx-border-color: #c5cad1;
    -fx-border-radius: 6px;
    -fx-background-color: #fafbfc;
}
```
**Use Case**: Login/Register form fields

#### Error State
```css
.text-field:error {
    -fx-border-color: #e74c3c;
    -fx-border-width: 2;
}
```
**Use Case**: Form validation errors

---

### Card Containers

#### Info Card
```css
.info-card {
    -fx-background-color: white;
    -fx-border-color: #d0d7de;
    -fx-border-radius: 8px;
    -fx-padding: 16px;
    -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.08), 4, 0, 0, 2);
}
```
**Use Case**: Account details, card info displays

#### Section Card
```css
.section-card {
    -fx-background-color: white;
    -fx-border-color: #d0d7de;
    -fx-border-radius: 8px;
    -fx-padding: 18px;
    -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.08), 4, 0, 0, 2);
}
```
**Use Case**: "Recent Transactions", "Payments", sections

#### Action Card
```css
.action-card {
    -fx-background-color: white;
    -fx-border-color: #e0e6ed;
    -fx-border-radius: 8px;
    -fx-padding: 16px;
}

.action-card:hover {
    -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.12), 6, 0, 0, 3);
}
```
**Use Case**: Interactive cards like "Transfer Funds", "Add Beneficiary"

#### Status Card
```css
.status-card {
    -fx-background-color: #f8f9fa;
    -fx-border-color: #d0d7de;
    -fx-border-radius: 8px;
    -fx-padding: 16px;
}
```
**Use Case**: Status messages, notifications

---

### Labels & Text

#### Section Title
```css
.section-title {
    -fx-font-size: 16px;
    -fx-font-weight: bold;
    -fx-text-fill: #1a1a1a;
}
```

#### Action Title
```css
.action-title {
    -fx-font-size: 14px;
    -fx-font-weight: bold;
    -fx-text-fill: #0066cc;
}
```

#### Info Label (Small Gray)
```css
.info-label {
    -fx-font-size: 11px;
    -fx-text-fill: #666666;
    -fx-font-weight: bold;
}
```

#### Info Value
```css
.info-value {
    -fx-font-size: 13px;
    -fx-font-weight: bold;
    -fx-text-fill: #1a1a1a;
}
```

#### Account Balance
```css
.account-balance {
    -fx-font-size: 18px;
    -fx-font-weight: bold;
    -fx-text-fill: #27ae60;
}
```

#### Status Labels
```css
.label-success {
    -fx-text-fill: #27ae60;  /* Green */
    -fx-font-weight: bold;
}

.label-danger {
    -fx-text-fill: #e74c3c;  /* Red */
    -fx-font-weight: bold;
}

.label-warning {
    -fx-text-fill: #f39c12;  /* Orange */
    -fx-font-weight: bold;
}
```

---

### Lists & Collections

#### List View
```css
.list-view {
    -fx-border-color: #d0d7de;
    -fx-border-radius: 6px;
    -fx-control-inner-background: #ffffff;
}

.list-cell {
    -fx-padding: 10px;
    -fx-border-color: #f0f0f0;
    -fx-border-width: 0 0 1 0;
}

.list-cell:filled:selected {
    -fx-background-color: #e3f2fd;
    -fx-text-fill: #0066cc;
}
```

#### Transactions List
```css
.transactions-list {
    -fx-border-color: #d0d7de;
    -fx-border-radius: 6px;
}
```

---

### Combo Box (Dropdown)

```css
.combo-box {
    -fx-padding: 10px;
    -fx-border-color: #c5cad1;
    -fx-border-radius: 6px;
    -fx-background-color: #ffffff;
}

.combo-box:focused {
    -fx-border-color: #0066cc;
    -fx-border-width: 2;
}

.combo-box-popup .list-view .list-cell:filled:selected {
    -fx-background-color: #0066cc;
    -fx-text-fill: white;
}
```

---

### Progress Indicators

```css
.progress-bar {
    -fx-control-inner-background: #ecf0f1;
}

.progress-bar .bar {
    -fx-background-color: linear-gradient(to right, #0066cc, #0052a3);
    -fx-border-radius: 3;
}

.progress-indicator {
    -fx-progress-color: #0066cc;
}
```

---

### Alert Messages

#### Success Alert
```css
.alert-success {
    -fx-background-color: #d4edda;
    -fx-text-fill: #155724;
    -fx-border-color: #c3e6cb;
}
```

#### Danger Alert
```css
.alert-danger {
    -fx-background-color: #f8d7da;
    -fx-text-fill: #721c24;
    -fx-border-color: #f5c6cb;
}
```

#### Warning Alert
```css
.alert-warning {
    -fx-background-color: #fff3cd;
    -fx-text-fill: #856404;
    -fx-border-color: #ffeeba;
}
```

---

## 🎬 Effects & Animations

### Drop Shadow
```css
-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 5, 0, 0, 2);
```
Creates soft shadow for depth

### Focus State
```css
:focused {
    -fx-border-color: #0066cc;
    -fx-effect: dropshadow(gaussian, rgba(0, 102, 204, 0.25), 6, 0, 0, 2);
}
```
Provides visual feedback when element has focus

### Hover State
```css
:hover {
    -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 8, 0, 0, 3);
}
```
Indicates interactive elements

---

## 🎨 Styling Best Practices

### 1. **Use CSS Classes Over Inline Styles**
```xml
<!-- ❌ Bad -->
<Button style="-fx-background-color: #0066cc; -fx-text-fill: white;" />

<!-- ✅ Good -->
<Button styleClass="action-button" />
```

### 2. **Maintain Consistency**
- Use the defined color palette
- Apply consistent padding and margins
- Follow border radius conventions (6px for modern look)

### 3. **Responsive Design**
- Cards should adapt to window size
- Use `HBox.hgrow` and `VBox.vgrow` for flexibility
- Consider mobile/tablet layouts

### 4. **Accessibility**
- Sufficient contrast (minimum 4.5:1 for text)
- Clear focus indicators
- Readable font sizes (12px minimum)

### 5. **Performance**
- Avoid complex gradients on many elements
- Use simple shadows (gaussian blur)
- Cache complex effects when possible

---

## 🔄 Button State Transitions

### Normal State → Hover → Pressed
```
Normal:    -fx-background-color: #0066cc
           -fx-effect: normal shadow

Hover:     -fx-background-color: #0052a3
           -fx-effect: stronger shadow

Pressed:   -fx-background-color: #003d7a
           -fx-scale-x: 0.98
           -fx-scale-y: 0.98
```

---

## 🌓 Dark Mode (Future Enhancement)

```css
.dark-theme {
    -fx-background-color: #1a1a1a;
}

.dark-theme .card {
    -fx-background-color: #2a2a2a;
    -fx-border-color: #404040;
}

.dark-theme .label {
    -fx-text-fill: #ecf0f1;
}
```

---

## 📱 Responsive Classes

### For Different Screen Sizes

```css
/* Tablets (768px and below) */
@media (max-width: 768px) {
    .button { -fx-font-size: 11px; }
    .label-title { -fx-font-size: 18px; }
    .card { -fx-padding: 10; }
}

/* Mobile (480px and below) */
@media (max-width: 480px) {
    .button { -fx-font-size: 10px; }
    .label-title { -fx-font-size: 16px; }
    .card { -fx-padding: 8; }
}
```

---

## 🛠️ How to Customize

### Change Primary Color
1. Open `styles.css`
2. Find color definitions at the top
3. Replace `#0066cc` with your color throughout
4. Example: Change to `#007bff` for lighter blue

### Change Button Style
1. Locate `.action-button` in CSS
2. Modify:
   - `-fx-background-color`: Change background
   - `-fx-padding`: Change size
   - `-fx-font-size`: Change text size

### Add New Component Style
1. Add new class to `styles.css`:
```css
.my-button {
    -fx-background-color: #your-color;
    -fx-padding: 10px 20px;
    -fx-border-radius: 6px;
    -fx-font-weight: bold;
}
```

2. Use in FXML:
```xml
<Button text="My Button" styleClass="my-button" />
```

---

## 📋 Styling Checklist

When adding new components, ensure:
- [ ] Consistent color scheme applied
- [ ] Proper padding and spacing (8px, 12px, 16px, 20px)
- [ ] Border radius of 6px or 8px
- [ ] Drop shadow for depth
- [ ] Hover effects for interactive elements
- [ ] Focus indicators for accessibility
- [ ] Dark text on light backgrounds (≥ 4.5:1 contrast)
- [ ] Font size ≥ 12px for readability
- [ ] Bottom padding on cards for visual separation

---

## 🚀 Performance Tips

1. **Minimize Shadows** - Complex shadows can impact performance
2. **Use Solid Colors** - Solid colors render faster than gradients
3. **Cache Stylesheets** - Load CSS once, not repeatedly
4. **Avoid Deep Nesting** - Deep selector chains slow down CSS matching
5. **Use Specific Selectors** - More specific = faster matching

---

## 📞 Need Help?

If you need to modify specific styles:
1. Find the element in FXML
2. Search for its `styleClass` or `style` attribute
3. Find the corresponding CSS rule
4. Modify and test
5. Consider the impact on other elements

---

## ✅ Final Notes

- All improvements maintain professional banking aesthetic
- Color scheme follows modern UI/UX best practices
- Styling is responsive and accessible
- Performance optimized for smooth interactions
- Consistent with industry standards

**Happy styling! 🎨**
