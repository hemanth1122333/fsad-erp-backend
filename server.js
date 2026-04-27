const express = require('express');
const cors = require('cors');

const app = express();
const PORT = 8080;

// Enable CORS for all routes
app.use(cors());
app.use(express.json());

// Mock captcha data
const challenges = new Map();

app.get('/api/auth/captcha-images', (req, res) => {
  const sessionId = 'demo-session-' + Date.now();
  const images = [
    { id: '1', url: '/api/auth/captcha-images/' + sessionId + '/1' },
    { id: '2', url: '/api/auth/captcha-images/' + sessionId + '/2' },
    { id: '3', url: '/api/auth/captcha-images/' + sessionId + '/3' },
    { id: '4', url: '/api/auth/captcha-images/' + sessionId + '/4' },
    { id: '5', url: '/api/auth/captcha-images/' + sessionId + '/5' },
    { id: '6', url: '/api/auth/captcha-images/' + sessionId + '/6' },
    { id: '7', url: '/api/auth/captcha-images/' + sessionId + '/7' },
    { id: '8', url: '/api/auth/captcha-images/' + sessionId + '/8' },
    { id: '9', url: '/api/auth/captcha-images/' + sessionId + '/9' }
  ];

  challenges.set(sessionId, { correctIds: ['2', '4', '9'] }); // traffic lights

  res.json({
    sessionId,
    question: 'Select all images with traffic lights',
    images
  });
});

app.get('/api/auth/captcha-images/:sessionId/:imageId', (req, res) => {
  const { imageId } = req.params;

  const svgImages = {
    '1': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120" width="120" height="120"><rect width="120" height="120" rx="20" fill="#f8fafc"/><rect x="6" y="6" width="108" height="108" rx="16" fill="white" stroke="#cbd5e1"/><circle cx="30" cy="72" r="14" fill="none" stroke="#2563eb" stroke-width="6"/><circle cx="82" cy="72" r="14" fill="none" stroke="#2563eb" stroke-width="6"/><path d="M30 72 L50 40 L66 72 Z" fill="none" stroke="#111827" stroke-width="5" stroke-linecap="round" stroke-linejoin="round"/><path d="M50 40 L72 40" fill="none" stroke="#111827" stroke-width="5" stroke-linecap="round"/></svg>`,
    '2': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120" width="120" height="120"><rect width="120" height="120" rx="20" fill="#f8fafc"/><rect x="6" y="6" width="108" height="108" rx="16" fill="white" stroke="#cbd5e1"/><rect x="36" y="14" width="52" height="72" rx="14" fill="#111827"/><circle cx="62" cy="30" r="10" fill="#ef4444"/><circle cx="62" cy="50" r="10" fill="#f59e0b"/><circle cx="62" cy="70" r="10" fill="#22c55e"/><rect x="58" y="86" width="8" height="38" rx="4" fill="#6b7280"/></svg>`,
    '3': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120" width="120" height="120"><rect width="120" height="120" rx="20" fill="#f8fafc"/><rect x="6" y="6" width="108" height="108" rx="16" fill="white" stroke="#cbd5e1"/><rect x="20" y="54" width="72" height="20" rx="8" fill="#2563eb"/><path d="M34 54 L44 38 H68 L78 54 Z" fill="#60a5fa"/><circle cx="36" cy="78" r="8" fill="#111827"/><circle cx="76" cy="78" r="8" fill="#111827"/></svg>`,
    '4': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120" width="120" height="120"><rect width="120" height="120" rx="20" fill="#f8fafc"/><rect x="6" y="6" width="108" height="108" rx="16" fill="white" stroke="#cbd5e1"/><rect x="36" y="14" width="52" height="72" rx="14" fill="#111827"/><circle cx="62" cy="30" r="10" fill="#ef4444"/><circle cx="62" cy="50" r="10" fill="#f59e0b"/><circle cx="62" cy="70" r="10" fill="#22c55e"/><rect x="58" y="86" width="8" height="38" rx="4" fill="#6b7280"/></svg>`,
    '5': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120" width="120" height="120"><rect width="120" height="120" rx="20" fill="#f8fafc"/><rect x="6" y="6" width="108" height="108" rx="16" fill="white" stroke="#cbd5e1"/><rect x="18" y="28" width="76" height="46" rx="10" fill="#f59e0b"/><rect x="26" y="36" width="14" height="12" fill="#fff7ed"/><rect x="44" y="36" width="14" height="12" fill="#fff7ed"/><rect x="62" y="36" width="14" height="12" fill="#fff7ed"/><circle cx="32" cy="78" r="7" fill="#111827"/><circle cx="78" cy="78" r="7" fill="#111827"/></svg>`,
    '6': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120" width="120" height="120"><rect width="120" height="120" rx="20" fill="#f8fafc"/><rect x="6" y="6" width="108" height="108" rx="16" fill="white" stroke="#cbd5e1"/><rect x="54" y="62" width="12" height="34" rx="4" fill="#92400e"/><circle cx="60" cy="42" r="24" fill="#22c55e"/><circle cx="42" cy="48" r="16" fill="#16a34a"/><circle cx="78" cy="48" r="16" fill="#16a34a"/></svg>`,
    '7': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120" width="120" height="120"><rect width="120" height="120" rx="20" fill="#f8fafc"/><rect x="6" y="6" width="108" height="108" rx="16" fill="white" stroke="#cbd5e1"/><circle cx="36" cy="42" r="12" fill="#d97706"/><circle cx="84" cy="42" r="12" fill="#d97706"/><path d="M30 50 H82 V78 H30 Z" fill="#f59e0b"/><circle cx="46" cy="62" r="4" fill="#111827"/><circle cx="66" cy="62" r="4" fill="#111827"/><path d="M52 70 Q56 76 60 70" fill="none" stroke="#111827" stroke-width="3" stroke-linecap="round"/></svg>`,
    '8': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120" width="120" height="120"><rect width="120" height="120" rx="20" fill="#f8fafc"/><rect x="6" y="6" width="108" height="108" rx="16" fill="white" stroke="#cbd5e1"/><rect x="24" y="34" width="72" height="56" rx="6" fill="#475569"/><polygon points="60,18 26,36 94,36" fill="#f97316"/><rect x="54" y="54" width="12" height="36" fill="#e2e8f0"/><rect x="34" y="48" width="12" height="12" fill="#e2e8f0"/><rect x="74" y="48" width="12" height="12" fill="#e2e8f0"/></svg>`,
    '9': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120" width="120" height="120"><rect width="120" height="120" rx="20" fill="#f8fafc"/><rect x="6" y="6" width="108" height="108" rx="16" fill="white" stroke="#cbd5e1"/><rect x="36" y="14" width="52" height="72" rx="14" fill="#111827"/><circle cx="62" cy="30" r="10" fill="#ef4444"/><circle cx="62" cy="50" r="10" fill="#f59e0b"/><circle cx="62" cy="70" r="10" fill="#22c55e"/><rect x="58" y="86" width="8" height="38" rx="4" fill="#6b7280"/></svg>`
  };

  const svg = svgImages[imageId] || svgImages['1'];
  res.setHeader('Content-Type', 'image/svg+xml');
  res.send(svg);
});

app.post('/api/auth/verify-captcha', (req, res) => {
  const { sessionId, selectedImageIds } = req.body;
  const challenge = challenges.get(sessionId);

  if (!challenge) {
    return res.json({ valid: false, message: 'Captcha session expired' });
  }

  const isValid = JSON.stringify(selectedImageIds.sort()) === JSON.stringify(challenge.correctIds.sort());
  challenges.delete(sessionId);

  res.json({
    valid: isValid,
    message: isValid ? 'Captcha verified' : 'Captcha is incorrect'
  });
});

// Mock auth endpoints
app.post('/api/auth/signup', (req, res) => {
  res.json({
    token: 'mock-jwt-token',
    user: { id: 1, email: req.body.email, role: req.body.role },
    homeRoute: '/dashboard'
  });
});

app.post('/api/auth/signin', (req, res) => {
  res.json({
    token: 'mock-jwt-token',
    user: { id: 1, email: req.body.email, role: 'student' },
    homeRoute: '/dashboard'
  });
});

// Mock dashboard endpoint
app.get('/api/dashboard/summary', (req, res) => {
  res.json({
    students: 42,
    teachers: 8,
    classes: 12,
    assignments: 25,
    submissions: 156,
    notifications: 7,
    recentLogs: [
      'SIGNUP - New student registered: john.doe@school.edu',
      'UPDATE - Assignment deadline extended for Math 101',
      'CREATE - New class created: Physics 201',
      'DELETE - Old notification cleaned up',
      'UPDATE - Student grade updated in Chemistry 101'
    ]
  });
});

app.listen(PORT, () => {
  console.log(`Mock backend server running on http://localhost:${PORT}`);
  console.log('Demo accounts:');
  console.log('  Admin: admin@erp.com / 123');
  console.log('  Teacher: teacher@erp.com / 123');
  console.log('  Student: student@erp.com / 123');
});