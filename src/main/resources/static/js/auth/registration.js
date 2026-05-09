export async function registration(formData){
    return  await fetch("/api/auth/registration", {
        method: "POST",
        credentials: "include",
        body: formData
    });
}