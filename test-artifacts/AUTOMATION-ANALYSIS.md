# Automation Feasibility Analysis
## Project: Vintelix — SCRUM-7 & SCRUM-8

---

## Summary

| Metric | Count |
|---|---|
| Total Test Cases | 79 |
| Automatable Test Cases | 62 |
| Non-Automatable Test Cases | 17 |
| **Automation Coverage** | **78.5%** |

---

## Automatable Test Cases (62)

### Selenium UI — Login (SCRUM-7): 30 automatable

| TC ID | Test Name |
|---|---|
| SCRUM7-TC-001 | Login page loads at /login |
| SCRUM7-TC-002 | Username/Email field present |
| SCRUM7-TC-003 | Password field present |
| SCRUM7-TC-004 | Login button present and enabled |
| SCRUM7-TC-005 | Login with valid username |
| SCRUM7-TC-006 | Login with valid email |
| SCRUM7-TC-007 | Invalid password shows error |
| SCRUM7-TC-008 | Non-existent user shows error |
| SCRUM7-TC-009 | Empty username field validation |
| SCRUM7-TC-010 | Empty password field validation |
| SCRUM7-TC-011 | Both fields empty validation |
| SCRUM7-TC-012 | Password visibility toggle |
| SCRUM7-TC-014 | Error message is generic (no field reveal) |
| SCRUM7-TC-015 | Token stored in sessionStorage |
| SCRUM7-TC-016 | Unauthenticated redirect to /login |
| SCRUM7-TC-017 | Authenticated user redirected from /login |
| SCRUM7-TC-018 | Invalid email format validation |
| SCRUM7-TC-019 | Valid email format — no format error |
| SCRUM7-TC-020 | Username without @ — no email error |
| SCRUM7-TC-022 | ARIA labels present |
| SCRUM7-TC-023 | Keyboard Tab navigation |
| SCRUM7-TC-024 | Enter key submits form |
| SCRUM7-TC-025 | Forgot password link navigates |
| SCRUM7-TC-026 | Sign up link navigates |
| SCRUM7-TC-027 | Responsive mobile (375x667) |
| SCRUM7-TC-028 | Responsive tablet (768x1024) |
| SCRUM7-TC-029 | XSS in username field not executed |
| SCRUM7-TC-031 | Very long username handled gracefully |
| SCRUM7-TC-032 | Whitespace-only username validation |
| SCRUM7-TC-033 | Back button after login behavior |

### Selenium UI — Forgot Password: 3 automatable

| TC ID | Test Name |
|---|---|
| SCRUM7-TC-048 | Forgot password page loads |
| SCRUM7-TC-049 | Forgot password email validation |
| SCRUM7-TC-050 | Forgot password success screen |

### Selenium UI — Dashboard (SCRUM-8): 19 automatable

| TC ID | Test Name |
|---|---|
| SCRUM8-TC-001 | Dashboard protected — unauthenticated redirect |
| SCRUM8-TC-002 | Dashboard loads after login |
| SCRUM8-TC-003 | Header bar visible |
| SCRUM8-TC-004 | Header shows username |
| SCRUM8-TC-005 | Header shows GMT date and time |
| SCRUM8-TC-006 | GMT clock updates live |
| SCRUM8-TC-007 | GMT time correct regardless of locale |
| SCRUM8-TC-008 | Left navigation panel visible |
| SCRUM8-TC-009 | IMDA Entities present |
| SCRUM8-TC-010 | IMDA Patterns present |
| SCRUM8-TC-011 | Navigation item order |
| SCRUM8-TC-012 | Clicking Entities navigates to /entities |
| SCRUM8-TC-013 | Clicking Patterns navigates to /patterns |
| SCRUM8-TC-014 | Active state on /entities |
| SCRUM8-TC-015 | Active state on /patterns |
| SCRUM8-TC-019 | /entities route protection |
| SCRUM8-TC-020 | /patterns route protection |
| SCRUM8-TC-021 | Logout clears session and redirects |
| SCRUM8-TC-024 | Missing username shows fallback |

### RestAssured API — Login & Signup: 15 automatable

| TC ID | Test Name |
|---|---|
| SCRUM7-TC-035 | API login valid username |
| SCRUM7-TC-036 | API login valid email |
| SCRUM7-TC-037 | API login wrong password — 401 |
| SCRUM7-TC-038 | API login non-existent user — 401 |
| SCRUM7-TC-039 | API login missing usernameOrEmail — 400 |
| SCRUM7-TC-040 | API login missing password — 400 |
| SCRUM7-TC-041 | API login empty body — 400 |
| SCRUM7-TC-042 | API rate limiting — 429 |
| SCRUM7-TC-043 | Token is valid JWT format |
| SCRUM7-TC-044 | Response schema validation |
| SCRUM7-TC-045 | Signup valid data — 200/201 |
| SCRUM7-TC-046 | Signup duplicate email — 409 |
| SCRUM7-TC-047 | Signup missing field — 400 |
| SCRUM7-TC-029 (API) | SQL injection rejected |
| SCRUM7-TC-031 (API) | Very long username — no 500 |

---

## Non-Automatable Test Cases (17)

| TC ID | Test Name | Reason |
|---|---|---|
| SCRUM7-TC-013 | Loading state visible during API call | Requires precise network throttling; timing-sensitive; flaky in automation |
| SCRUM7-TC-021 | Rate limiting after N failed attempts (UI) | Requires coordinated state management between browser sessions; better tested at API level |
| SCRUM7-TC-030 | SQL injection in login field | Security validation confirmed at API layer (TC-029 API); UI portion is redundant and unreliable via Selenium |
| SCRUM7-TC-034 | Session persistence across tab close/reopen | Browser tab lifecycle is not controllable via standard WebDriver APIs |
| SCRUM8-TC-016 | Active state on /dashboard itself | Requires subjective visual design comparison |
| SCRUM8-TC-017 | Hover state on nav items | CSS :hover pseudo-class cannot be asserted in most Selenium setups; requires visual regression tool (Percy/Applitools) |
| SCRUM8-TC-018 | Focus state visible on nav items (WCAG) | CSS :focus visual appearance requires visual regression testing |
| SCRUM8-TC-022 | Sidebar collapsible on mobile | CSS media-query transitions and DOM collapse behavior requires visual verification |
| SCRUM8-TC-023 | Full desktop layout | Layout fidelity (spacing, alignment) requires visual regression tool |
| SCRUM8-TC-025 | Expired session redirect | Requires mocking server-side token expiry or time manipulation |
| SCRUM8-TC-026 | ARIA roles on navigation | Partially automatable via Selenium getAttribute; full ARIA tree testing requires accessibility testing tools (axe-core) |
| SCRUM8-TC-027 | Keyboard navigation through entire dashboard | Complex multi-step keyboard flow; maintainability risk; better via dedicated a11y audit |
| SCRUM8-TC-028 | App brand/logo visible | Visual presence check — presence can be automated but brand correctness requires visual review |
| SCRUM8-TC-029 | Page/tab title correct | Low risk; difficult to keep stable across environments |
| SCRUM7-TC-022 (full) | Full WCAG AA color contrast compliance | Requires dedicated accessibility scanner (axe, Lighthouse); not feasible with standard Selenium |
| SCRUM8-TC-013 (design) | Dashboard matches approved Figma design | Pixel-perfect design validation requires visual regression tool (Percy, Applitools) |
| SCRUM7-TC-028 (tablet) | Tablet responsive layout fidelity | Visual layout correctness requires visual testing beyond Selenium assertions |

---

## Notes

1. **Rate limiting (TC-042)** is included in RestAssured automation but flagged: the threshold count must match the server's configured value. Update `rateLimitThreshold` in `LoginApiTest` accordingly.
2. **Visual/accessibility tests** are recommended to complement Selenium with `axe-core` WebDriver integration or a visual regression SaaS.
3. **TC-021 UI Rate Limiting** is tested at API level (TC-042) which is more reliable and complete.
4. **GitHub repos were private** and could not be accessed; all test cases are based on the full Jira ticket descriptions and implementation notes in the ticket comments. Update locators in Page Objects once source code is available.
