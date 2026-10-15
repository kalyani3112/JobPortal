
// =====================================
// NAVIGATION
// =====================================

function hideAll() {
    const sections = [
        "home",
        "login",
        "register",
        "apply",
        "myApplications"
    ];

    sections.forEach(id => {
        const section = document.getElementById(id);

        if (section) {
            section.style.display = "none";
        }
    });
}

function showHome() {
    hideAll();
    document.getElementById("home").style.display = "block";
    window.scrollTo({ top: 0, behavior: "smooth" });
}

function showJobs() {
    hideAll();
    document.getElementById("home").style.display = "block";

    setTimeout(() => {
        const jobs = document.querySelector(".jobs");

        if (jobs) {
            jobs.scrollIntoView({ behavior: "smooth" });
        }
    }, 100);
}

function showLogin() {
    hideAll();
    document.getElementById("login").style.display = "block";
}

function showRegister() {
    hideAll();
    document.getElementById("register").style.display = "block";
}

// =====================================
// MY APPLICATIONS
// =====================================

function showMyApplications() {
    const studentId = sessionStorage.getItem("studentId");

    if (!studentId) {
        alert("Please log in to view your applications.");
        showLogin();
        return;
    }

    hideAll();

    const section = document.getElementById("myApplications");

    if (!section) {
        alert("My Applications section is missing from index.html.");
        return;
    }

    section.style.display = "block";
    loadMyApplications();
}

async function loadMyApplications() {
    const container = document.getElementById("applicationsList");
    const studentId = sessionStorage.getItem("studentId");

    if (!container) {
        console.error("Missing element: applicationsList");
        return;
    }

    if (!studentId) {
        container.textContent = "Please log in to view your applications.";
        return;
    }

    container.textContent = "Loading applications...";

    try {
        const response = await fetch(
            "http://localhost:8080/api/my-applications?studentId=" +
            encodeURIComponent(studentId)
        );

        const data = await response.json();

        if (!response.ok) {
            throw new Error(
                data.message || "Failed to load applications."
            );
        }

        if (!Array.isArray(data)) {
            throw new Error(
                "The server did not return an applications list."
            );
        }

        container.innerHTML = "";

        if (data.length === 0) {
            const message = document.createElement("p");
            message.textContent =
                "You have not applied for any jobs yet.";

            container.appendChild(message);
            return;
        }

        const table = document.createElement("table");

        table.style.width = "100%";
        table.style.borderCollapse = "collapse";
        table.style.marginTop = "20px";

        const thead = document.createElement("thead");
        const headerRow = document.createElement("tr");

        ["Job Title", "Company", "Applied Date", "Status"]
            .forEach(heading => {
                const th = document.createElement("th");

                th.textContent = heading;
                th.style.padding = "12px";
                th.style.border = "1px solid #ddd";
                th.style.textAlign = "left";

                headerRow.appendChild(th);
            });

        thead.appendChild(headerRow);
        table.appendChild(thead);

        const tbody = document.createElement("tbody");

        data.forEach(application => {
            const row = document.createElement("tr");

            const values = [
                application.jobTitle,
                application.company,
                application.appliedDate,
                application.status
            ];

            values.forEach(value => {
                const cell = document.createElement("td");

                cell.textContent =
                    value == null || value === ""
                        ? "—"
                        : String(value);

                cell.style.padding = "12px";
                cell.style.border = "1px solid #ddd";

                row.appendChild(cell);
            });

            tbody.appendChild(row);
        });

        table.appendChild(tbody);

        // Allow horizontal scrolling on smaller screens.
        const tableWrapper = document.createElement("div");
        tableWrapper.style.overflowX = "auto";
        tableWrapper.appendChild(table);

        container.appendChild(tableWrapper);

    } catch (error) {
        console.error("My Applications error:", error);

        container.innerHTML = "";

        const message = document.createElement("p");
        message.textContent =
            "Unable to load applications. Check the backend and try again.";

        container.appendChild(message);
    }
}

// =====================================
// APPLY FOR A JOB
// =====================================

function applyJob(jobId, jobName) {
    const studentId = sessionStorage.getItem("studentId");

    if (!studentId) {
        alert("Please log in before applying for a job.");
        showLogin();
        return;
    }

    hideAll();

    document.getElementById("apply").style.display = "block";

    const selectedJob = document.getElementById("selectedJob");

    selectedJob.textContent = "You are applying for: " + jobName;
    selectedJob.dataset.jobId = String(jobId);

    window.scrollTo({ top: 0, behavior: "smooth" });
}

// =====================================
// LOGIN
// =====================================

async function login() {
    const email = document.getElementById("loginEmail").value.trim();
    const password = document.getElementById("loginPassword").value;

    if (!email || !password) {
        alert("Please enter email and password.");
        return;
    }

    try {
        const response = await fetch(
            "http://localhost:8080/api/login",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ email, password })
            }
        );

        const data = await response.json();

        if (!response.ok) {
            alert(data.message || "Invalid email or password.");
            return;
        }

        if (data.studentId == null) {
            alert("Login response did not contain a student ID.");
            return;
        }

        sessionStorage.setItem("studentId", String(data.studentId));

        alert(data.message || "Login successful!");
        showHome();

    } catch (error) {
        console.error("Login error:", error);

        alert(
            "Cannot connect to the server. Make sure the Java backend is running."
        );
    }
}

// =====================================
// REGISTER
// =====================================

async function register() {
    const name = document.getElementById("registerName").value.trim();
    const email = document.getElementById("registerEmail").value.trim();
    const password = document.getElementById("registerPassword").value;

    if (!name || !email || !password) {
        alert("Please fill in all registration fields.");
        return;
    }

    try {
        const response = await fetch(
            "http://localhost:8080/api/register",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ name, email, password })
            }
        );

        const data = await response.json();

        if (!response.ok) {
            alert(data.message || "Registration failed.");
            return;
        }

        alert(data.message || "Registration successful!");

        if (data.message === "Registration successful") {
            document.getElementById("loginEmail").value = email;
            document.getElementById("loginPassword").value = "";
            showLogin();
        }

    } catch (error) {
        console.error("Registration error:", error);

        alert(
            "Cannot connect to the server. Make sure the Java backend is running."
        );
    }
}

// =====================================
// SUBMIT JOB APPLICATION
// =====================================

async function submitApplication() {
    const name = document.getElementById("applicantName").value.trim();
    const email = document.getElementById("applicantEmail").value.trim();
    const skills = document.getElementById("applicantSkills").value.trim();

    const selectedJob = document.getElementById("selectedJob");
    const jobId = selectedJob.dataset.jobId;
    const studentId = sessionStorage.getItem("studentId");

    if (!studentId) {
        alert("Please log in before applying for a job.");
        showLogin();
        return;
    }

    if (!name || !email || !skills) {
        alert("Please fill in all application details.");
        return;
    }

    if (!jobId) {
        alert("Please select a job first.");
        return;
    }

    try {
        const response = await fetch(
            "http://localhost:8080/api/apply",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    studentId: String(studentId),
                    jobId: String(jobId)
                })
            }
        );

        const data = await response.json();

        if (!response.ok) {
            alert(data.message || "Application failed.");
            return;
        }

        alert(data.message || "Application submitted successfully!");

        if (
            typeof data.message === "string" &&
            data.message.toLowerCase().includes("success")
        ) {
            showHome();
        }

    } catch (error) {
        console.error("Application error:", error);

        alert(
            "Unable to submit your application. Check the backend."
        );
    }
}

// =====================================
// SEARCH JOBS
// =====================================

function searchJobs() {
    const search = document
        .getElementById("searchInput")
        .value.trim()
        .toLowerCase();

    document.querySelectorAll(".job-card").forEach(job => {
        job.style.display =
            job.innerText.toLowerCase().includes(search)
                ? ""
                : "none";
    });
}

// =====================================
// LOAD JOBS FROM JAVA BACKEND
// =====================================

async function loadJobs() {
    try {
        const response = await fetch(
            "http://localhost:8080/api/jobs"
        );

        if (!response.ok) {
            throw new Error("Unable to load jobs.");
        }

        const jobs = await response.json();
        const container = document.querySelector(".jobs");

        if (!container) {
            console.error("Jobs container not found.");
            return;
        }

        container.innerHTML = "";

        jobs.forEach(job => {
            const card = document.createElement("div");
            card.className = "job-card";

            const title = document.createElement("h3");
            title.textContent = job.title || "Untitled Job";

            const company = document.createElement("p");
            company.textContent = "Company: " + (job.company || "—");

            const location = document.createElement("p");
            location.textContent = "Location: " + (job.location || "—");

            const skills = document.createElement("p");
            skills.textContent = "Skills: " + (job.skills || "—");

            const button = document.createElement("button");
            button.textContent = "Apply Now";

            button.addEventListener("click", () => {
                applyJob(job.id, job.title || "Selected Job");
            });

            card.append(title, company, location, skills, button);
            container.appendChild(card);
        });

    } catch (error) {
        console.error("Job loading error:", error);
    }
}

// =====================================
// START APPLICATION
// =====================================

document.addEventListener("DOMContentLoaded", () => {
    showHome();
    loadJobs();
});