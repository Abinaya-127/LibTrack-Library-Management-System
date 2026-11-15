const API = "http://localhost:8080/api";

function showPage(pageId, button) {
    document.querySelectorAll(".page").forEach(page => {
        page.classList.remove("active");
    });

    document.getElementById(pageId).classList.add("active");

    document.querySelectorAll(".nav-btn").forEach(btn => {
        btn.classList.remove("active");
    });

    button.classList.add("active");

    loadPageData(pageId);
}

async function apiRequest(endpoint, options = {}) {
    const response = await fetch(`${API}${endpoint}`, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            ...options.headers
        }
    });

    const result = await response.text();

    if (!response.ok) {
        throw new Error(result || "Something went wrong");
    }

    try {
        return JSON.parse(result);
    } catch {
        return result;
    }
}

function displayTable(containerId, records) {
    const container = document.getElementById(containerId);

    if (!records || records.length === 0) {
        container.innerHTML = "<p>No records found.</p>";
        return;
    }

    const columns = Object.keys(records[0]);

    let html = "<table><thead><tr>";

    columns.forEach(column => {
        html += `<th>${column.replaceAll("_", " ").toUpperCase()}</th>`;
    });

    html += "</tr></thead><tbody>";

    records.forEach(record => {
        html += "<tr>";

        columns.forEach(column => {
            html += `<td>${record[column] ?? "-"}</td>`;
        });

        html += "</tr>";
    });

    html += "</tbody></table>";

    container.innerHTML = html;
}

async function loadBooks() {
    const books = await apiRequest("/books");

    displayTable("booksTable", books);

    document.getElementById("totalBooks").textContent =
        books.reduce((sum, book) => sum + Number(book.quantity || 0), 0);
}

async function loadMembers() {
    const members = await apiRequest("/members");

    displayTable("membersTable", members);

    document.getElementById("totalMembers").textContent = members.length;
}

async function loadIssues() {
    const issues = await apiRequest("/issues");

    displayTable("issuesTable", issues);

    document.getElementById("totalIssues").textContent =
        issues.filter(issue => issue.status === "Issued").length;
}

async function loadReturns() {
    const returns = await apiRequest("/returns");

    displayTable("returnsTable", returns);
}

async function loadOverdue() {
    const overdue = await apiRequest("/overdue");

    displayTable("overdueTable", overdue);

    document.getElementById("totalOverdue").textContent = overdue.length;
}

async function loadPageData(pageId) {
    try {
        if (pageId === "dashboard") {
            await Promise.all([
                loadBooks(),
                loadMembers(),
                loadIssues(),
                loadOverdue()
            ]);
        }

        if (pageId === "books") await loadBooks();
        if (pageId === "members") await loadMembers();
        if (pageId === "issues") await loadIssues();
        if (pageId === "returns") await loadReturns();
        if (pageId === "overdue") await loadOverdue();

    } catch (error) {
        alert("Connection error: " + error.message);
    }
}

document.getElementById("bookForm").addEventListener("submit", async event => {
    event.preventDefault();

    try {
        const book = {
            title: document.getElementById("bookTitle").value,
            author: document.getElementById("bookAuthor").value,
            category: document.getElementById("bookCategory").value,
            quantity: Number(document.getElementById("bookQuantity").value)
        };

        const result = await apiRequest("/books", {
            method: "POST",
            body: JSON.stringify(book)
        });

        document.getElementById("bookMessage").textContent = result;
        event.target.reset();
        await loadBooks();

    } catch (error) {
        document.getElementById("bookMessage").textContent = error.message;
    }
});

document.getElementById("memberForm").addEventListener("submit", async event => {
    event.preventDefault();

    try {
        const member = {
            name: document.getElementById("memberName").value,
            email: document.getElementById("memberEmail").value,
            phone: document.getElementById("memberPhone").value
        };

        const result = await apiRequest("/members", {
            method: "POST",
            body: JSON.stringify(member)
        });

        document.getElementById("memberMessage").textContent = result;
        event.target.reset();
        await loadMembers();

    } catch (error) {
        document.getElementById("memberMessage").textContent = error.message;
    }
});

document.getElementById("issueForm").addEventListener("submit", async event => {
    event.preventDefault();

    try {
        const issue = {
            book_id: Number(document.getElementById("issueBookId").value),
            member_id: Number(document.getElementById("issueMemberId").value)
        };

        const result = await apiRequest("/issues", {
            method: "POST",
            body: JSON.stringify(issue)
        });

        document.getElementById("issueMessage").textContent = result;
        event.target.reset();
        await loadIssues();
        await loadBooks();

    } catch (error) {
        document.getElementById("issueMessage").textContent = error.message;
    }
});

document.getElementById("returnForm").addEventListener("submit", async event => {
    event.preventDefault();

    try {
        const data = {
            issue_id: Number(document.getElementById("returnIssueId").value)
        };

        const result = await apiRequest("/returns", {
            method: "POST",
            body: JSON.stringify(data)
        });

        document.getElementById("returnMessage").textContent = result;
        event.target.reset();

        await loadReturns();
        await loadBooks();
        await loadIssues();

    } catch (error) {
        document.getElementById("returnMessage").textContent = error.message;
    }
});

window.addEventListener("DOMContentLoaded", () => {
    loadPageData("dashboard");
});