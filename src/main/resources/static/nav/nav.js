(function(){
  async function loadNav(){
    try{
      const res = await fetch('/nav/navigation.html');
      if(!res.ok) throw new Error('Navigation not found');
      const html = await res.text();
      const container = document.getElementById('site-nav');
      if(!container) return;
      container.innerHTML = html;
      // attach toggle
      const toggle = container.querySelector('.nav-toggle');
      const links = container.querySelector('.nav-links');
      if(toggle && links){
        toggle.addEventListener('click', ()=> links.classList.toggle('show'));
      }
      // highlight active link based on data-active attribute
      const active = container.dataset.active;
      if(active){
        const link = container.querySelector(`a[data-name="${active}"]`);
        if(link) link.classList.add('active');
      }
    }catch(e){
      console.warn('Failed to load navbar',e);
    }
  }
  if(document.readyState==='loading'){
    document.addEventListener('DOMContentLoaded', loadNav);
  } else loadNav();
})();
