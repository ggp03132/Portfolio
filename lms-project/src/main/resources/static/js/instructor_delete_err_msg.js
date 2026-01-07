document.addEventListener("DOMContentLoaded", () => {
  const messageBox = document.getElementById("message-box");
  if (!messageBox) return;

  const errorMessage = messageBox.dataset.errorMessage;

  if (errorMessage && errorMessage.trim() !== "") {
    alert(errorMessage);
  }
});
